package com.example.ejb;

import jakarta.ejb.Stateless;
import jakarta.persistence.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.ejb.exception.ConcorrenciaDetectadaException;
import com.example.ejb.exception.ContaNaoEncontradaException;
import com.example.ejb.exception.SaldoInsuficienteException;
import com.example.ejb.exception.TransferenciaNaoPermitidaException;

import java.math.BigDecimal;

@Stateless
public class BeneficioEjbService {

    private static final Logger log = LoggerFactory.getLogger(BeneficioEjbService.class);

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Realiza transferência entre duas contas de benefício.
     */
    public void realizarTransferencia(Long origemId, Long destinoId, BigDecimal valor) {
        validarParametros(origemId, destinoId, valor);

        if (origemId.equals(destinoId)) {
            throw new TransferenciaNaoPermitidaException("Não é possível transferir para a mesma conta de benefício.");
        }

        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new TransferenciaNaoPermitidaException(
                    String.format("O valor da transferência deve ser positivo: %s", valor)
            );
        }

        try {
            BeneficioEjbService origem = buscarContaPorId(origemId);
            BeneficioEjbService destino = buscarContaPorId(destinoId);

            log.debug("[EJB] Contas encontradas - ORIGEM: {} (versão={}), DESTINO: {} (versão={})",
                    origem.getNome(), origem.getVersao(), destino.getNome(), destino.getVersao());

            origem.verificarAtiva();
            destino.verificarAtiva();

            // Lock otimista
            entityManager.lock(origem, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
            entityManager.lock(destino, LockModeType.OPTIMISTIC_FORCE_INCREMENT);

            // Validação de saldo
            if (origem.getSaldo().compareTo(valor) < 0) {
                throw new SaldoInsuficienteException("Saldo insuficiente na conta de origem.");
            }

            // Debitar e creditar
            origem.debitar(valor);
            destino.creditar(valor);

            entityManager.merge(origem);
            entityManager.merge(destino);
            entityManager.flush();

            log.info("[EJB] Transferência realizada: ORIGEM={} (saldo={}, versao={}), DESTINO={} (saldo={}, versao={})",
                    origemId, origem.getSaldo(), origem.getVersao(),
                    destinoId, destino.getSaldo(), destino.getVersao());

        } catch (OptimisticLockException e) {
            log.warn("[EJB] Conflito de concorrência na transferência: ORIGEM={}, DESTINO={}", origemId, destinoId);
            throw new ConcorrenciaDetectadaException("Conflito de concorrência detectado", e);

        } catch (ContaNaoEncontradaException | SaldoInsuficienteException | TransferenciaNaoPermitidaException e) {
            log.error("[EJB] Erro de validação na transferência: {}", e.getMessage());
            throw e;

        } catch (Exception e) {
            log.error("[EJB] Erro inesperado na transferência: ORIGEM={}, DESTINO={}, VALOR={}", origemId, destinoId, valor, e);
            throw new RuntimeException("Erro inesperado ao processar transferência", e);
        }
    }

    private BeneficioEjbService buscarContaPorId(Long id) {
        log.debug("[EJB] Buscando conta de benefício ID={}", id);
        BeneficioEjbService conta = entityManager.find(BeneficioEjbService.class, id, LockModeType.OPTIMISTIC);
        if (conta == null) {
            log.error("[EJB] Conta não encontrada ID={}", id);
            throw new ContaNaoEncontradaException(id);
        }
        log.debug("[EJB] Conta encontrada: {}", conta);
        return conta;
    }

    private void validarParametros(Long origemId, Long destinoId, BigDecimal valor) {
        if (origemId == null) {
            throw new TransferenciaNaoPermitidaException("ID da conta de origem não pode ser nulo.");
        }
        if (destinoId == null) {
            throw new TransferenciaNaoPermitidaException("ID da conta de destino não pode ser nulo.");
        }
        if (valor == null) {
            throw new TransferenciaNaoPermitidaException("Valor da transferência não pode ser nulo.");
        }
    }
}
