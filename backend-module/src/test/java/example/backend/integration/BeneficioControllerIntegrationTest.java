package example.backend.integration;


import com.example.backend.dto.TransferenciaRequest;
import com.example.backend.repository.BeneficioRepository;
import com.example.ejb.Beneficio;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BeneficioControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BeneficioRepository beneficioRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private ObjectMapper objectMapper;

    private Long origemId;
    private Long destinoId;
    private BigDecimal saldoInicialOrigem;
    private BigDecimal saldoInicialDestino;

    @BeforeEach
    void setUp() {
        beneficioRepository.deleteAll();
        beneficioRepository.flush();
        entityManager.clear();

        Beneficio origem = new Beneficio("Benefício Transporte", "Conta usada para transporte do funcionário", new BigDecimal("1200.00"));
        Beneficio destino = new Beneficio("Benefício Refeição", "Conta usada para refeição do funcionário", new BigDecimal("600.00"));

        origem = beneficioRepository.saveAndFlush(origem);
        destino = beneficioRepository.saveAndFlush(destino);

        origemId = origem.getId();
        destinoId = destino.getId();
        saldoInicialOrigem = origem.getSaldo();
        saldoInicialDestino = destino.getSaldo();
    }

    @Test
    @DisplayName("Deve realizar transferência via endpoint REST e atualizar saldos corretamente")
    void deveTransferirComSucessoViaEndpoint() throws Exception {
       
    	TransferenciaRequest request = new TransferenciaRequest(
    		    origemId,
    		    destinoId,
    		    new BigDecimal("150.00"),
    		    UUID.randomUUID().toString()
    		);

        
        mockMvc.perform(post("/api/v1/beneficios/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("")); 

      
        entityManager.clear();
        Beneficio origem = beneficioRepository.findById(origemId).orElseThrow();
        Beneficio destino = beneficioRepository.findById(destinoId).orElseThrow();

       
        BigDecimal esperadoOrigem = saldoInicialOrigem.subtract(new BigDecimal("150.00"));
        BigDecimal esperadoDestino = saldoInicialDestino.add(new BigDecimal("150.00"));

        assertThat(origem.getSaldo())
                .as("Saldo da origem deve ser reduzido corretamente")
                .isEqualByComparingTo(esperadoOrigem);

        assertThat(destino.getSaldo())
                .as("Saldo do destino deve ser acrescido corretamente")
                .isEqualByComparingTo(esperadoDestino);
    }

    @Test
    @DisplayName("Deve retornar erro 422 quando saldo for insuficiente")
    void deveRetornarErroQuandoSaldoInsuficiente() throws Exception {
    	TransferenciaRequest request = new TransferenciaRequest(
    		    origemId,
    		    destinoId,
    		    new BigDecimal("9999999.00"),
    		    UUID.randomUUID().toString()
    		);

        mockMvc.perform(post("/api/v1/beneficios/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @DisplayName("Deve retornar 404 quando benefício origem não existir")
    void deveRetornar404QuandoBeneficioNaoEncontrado() throws Exception {
    	TransferenciaRequest request = new TransferenciaRequest(
    		    9999L,
    		    destinoId,
    		    new BigDecimal("50.00"),
    		    UUID.randomUUID().toString()
    		);

        mockMvc.perform(post("/api/v1/beneficios/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }
}

