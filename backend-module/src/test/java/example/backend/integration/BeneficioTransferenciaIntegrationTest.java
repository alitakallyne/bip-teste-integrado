package example.backend.integration;


import com.example.backend.dto.TransferenciaRequest;
import com.example.backend.repository.BeneficioRepository;
import com.example.backend.service.BeneficioService;
import com.example.ejb.Beneficio;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static java.time.Duration.ofSeconds;

@SpringBootTest
@ActiveProfiles("test")
class BeneficioTransferenciaIntegrationTest {

    @Autowired
    private BeneficioService beneficioService;

    @Autowired
    private BeneficioRepository beneficioRepository;

    @Autowired
    private EntityManager entityManager;

    private Long origemId;
    private Long destinoId;
    private BigDecimal saldoInicialOrigem;
    private BigDecimal saldoInicialDestino;

    @BeforeEach
    void initDatabase() {
        beneficioRepository.deleteAll();
        beneficioRepository.flush();
        entityManager.clear();

        Beneficio origem = new Beneficio("Benefício Saúde", "Conta principal do colaborador", new BigDecimal("1500.00"));
        Beneficio destino = new Beneficio("Benefício Alimentação", "Conta de apoio", new BigDecimal("800.00"));

        origem = beneficioRepository.saveAndFlush(origem);
        destino = beneficioRepository.saveAndFlush(destino);

        origemId = origem.getId();
        destinoId = destino.getId();
        saldoInicialOrigem = origem.getSaldo();
        saldoInicialDestino = destino.getSaldo();

        assertThat(origem.getVersao())
                .as("A versão do benefício deve iniciar em 0")
                .isEqualTo(0L);
    }

    @Test
    @DisplayName("Deve evitar conflito de concorrência em 5 transferências simultâneas")
    void deveEvitarConflitoConcorrenteEmTransferencias() {
        assertTimeoutPreemptively(ofSeconds(15), () -> {

            int numThreads = 5;
            BigDecimal valorTransferencia = new BigDecimal("25.00");
            BigDecimal totalTransferido = valorTransferencia.multiply(new BigDecimal(numThreads));

            AtomicInteger sucesso = new AtomicInteger(0);
            AtomicInteger falhas = new AtomicInteger(0);

            ExecutorService executor = Executors.newFixedThreadPool(numThreads);
            List<CompletableFuture<Void>> futures = new ArrayList<>();

            for (int i = 0; i < numThreads; i++) {
                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    try {
                    	TransferenciaRequest req = new TransferenciaRequest(
                        		origemId,
                                destinoId,
                                valor,
                                UUID.randomUUID().toString()
                                );

                        beneficioService.transferirValor(req);
                        sucesso.incrementAndGet();

                    } catch (Exception e) {
                        falhas.incrementAndGet();
                    }
                }, executor);
                futures.add(future);
            }

            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                    .get(10, TimeUnit.SECONDS);

            executor.shutdown();
            executor.awaitTermination(5, TimeUnit.SECONDS);

            assertThat(sucesso.get())
                    .as("Todas as transferências devem ser processadas com sucesso")
                    .isEqualTo(numThreads);

            assertThat(falhas.get())
                    .as("Nenhuma transferência deve falhar após retry automático")
                    .isEqualTo(0);

            entityManager.clear();

            Beneficio origemAtualizado = beneficioRepository.findById(origemId).orElseThrow();
            Beneficio destinoAtualizado = beneficioRepository.findById(destinoId).orElseThrow();

            BigDecimal saldoEsperadoOrigem = saldoInicialOrigem.subtract(totalTransferido);
            BigDecimal saldoEsperadoDestino = saldoInicialDestino.add(totalTransferido);

            assertThat(origemAtualizado.getSaldo())
                    .as("Saldo final da conta origem deve ser reduzido corretamente")
                    .isEqualByComparingTo(saldoEsperadoOrigem);

            assertThat(destinoAtualizado.getSaldo())
                    .as("Saldo final da conta destino deve refletir as transferências")
                    .isEqualByComparingTo(saldoEsperadoDestino);

            assertThat(origemAtualizado.getVersao())
                    .as("Versão do registro origem deve ser incrementada conforme transferências")
                    .isEqualTo((long) numThreads);

            assertThat(destinoAtualizado.getVersao())
                    .as("Versão do registro destino deve ser incrementada conforme transferências")
                    .isEqualTo((long) numThreads);
        });
    }

    @Test
    @DisplayName("Deve realizar corretamente 10 transferências sequenciais")
    void deveExecutarTransferenciasSequenciais() {
        int qtd = 10;
        BigDecimal valor = new BigDecimal("10.00");
        BigDecimal total = valor.multiply(new BigDecimal(qtd));

        for (int i = 0; i < qtd; i++) {
            TransferenciaRequest req = new TransferenciaRequest(
            		origemId,
                    destinoId,
                    valor,
                    UUID.randomUUID().toString()
                    );

            beneficioService.transferirValor(req);
        }

        entityManager.clear();

        Beneficio origem = beneficioRepository.findById(origemId).orElseThrow();
        Beneficio destino = beneficioRepository.findById(destinoId).orElseThrow();

        assertThat(origem.getSaldo())
                .isEqualByComparingTo(saldoInicialOrigem.subtract(total));

        assertThat(destino.getSaldo())
                .isEqualByComparingTo(saldoInicialDestino.add(total));

        assertThat(origem.getVersao()).isEqualTo((long) qtd);
        assertThat(destino.getVersao()).isEqualTo((long) qtd);
    }

    @Test
    @DisplayName("Performance: deve executar 5 transferências em menos de 5 segundos")
    void deveManterBoaPerformance() {
        assertTimeoutPreemptively(ofSeconds(5), () -> {
            int numThreads = 5;
            BigDecimal valor = new BigDecimal("15.00");

            long inicio = System.currentTimeMillis();

            ExecutorService executor = Executors.newFixedThreadPool(numThreads);
            List<CompletableFuture<Void>> futures = new ArrayList<>();

            for (int i = 0; i < numThreads; i++) {
                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    TransferenciaRequest req = new TransferenciaRequest(
                    		origemId,
                            destinoId,
                            valor,
                            UUID.randomUUID().toString()
                            );

                    beneficioService.transferirValor(req);
                }, executor);

                futures.add(future);
            }

            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get();

            executor.shutdown();
            executor.awaitTermination(3, TimeUnit.SECONDS);

            long duracao = System.currentTimeMillis() - inicio;

            assertThat(duracao)
                    .as("Tempo total deve ser inferior a 5 segundos")
                    .isLessThan(5000L);
        });
    }
}

