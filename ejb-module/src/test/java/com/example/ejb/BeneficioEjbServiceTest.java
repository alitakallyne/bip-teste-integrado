package com.example.ejb;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.ejb.exception.ConcorrenciaDetectadaException;
import com.example.ejb.exception.ContaNaoEncontradaException;
import com.example.ejb.exception.SaldoInsuficienteException;
import com.example.ejb.exception.TransferenciaNaoPermitidaException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.OptimisticLockException;

@ExtendWith(MockitoExtension.class)
class BeneficioEjbServiceTest {

    @InjectMocks
    private BeneficioEjbService ejbService;

    @Mock
    private EntityManager entityManager;

    private Beneficio origem;
    private Beneficio destino;

    @BeforeEach
    void setUp() {
        origem = new Beneficio();
        origem.setId(3L);
        origem.setNome("Origem");
        origem.setSaldo(new BigDecimal("1000"));
        origem.setAtiva(true);

        destino = new Beneficio();
        destino.setId(4L);
        destino.setNome("Destino");
        destino.setSaldo(new BigDecimal("500"));
        destino.setAtiva(true);
    }

  
    @Test
    void deveTransferirComSucesso() {
        when(entityManager.find(Beneficio.class, 3L, LockModeType.OPTIMISTIC)).thenReturn(origem);
        when(entityManager.find(Beneficio.class, 4L, LockModeType.OPTIMISTIC)).thenReturn(destino);

        ejbService.realizarTransferencia(3L, 4L, new BigDecimal("200"));

        assertEquals(new BigDecimal("800"), origem.getSaldo());
        assertEquals(new BigDecimal("700"), destino.getSaldo());

        verify(entityManager).merge(origem);
        verify(entityManager).merge(destino);
        verify(entityManager).flush();
    }

    @Test
    void deveLancarSaldoInsuficiente() {
        when(entityManager.find(Beneficio.class, 3L, LockModeType.OPTIMISTIC)).thenReturn(origem);
        when(entityManager.find(Beneficio.class, 4L, LockModeType.OPTIMISTIC)).thenReturn(destino);

        assertThrows(SaldoInsuficienteException.class,
                () -> ejbService.realizarTransferencia(3L, 4L, new BigDecimal("2000")));
    }

 
    @Test
    void deveLancarContaNaoEncontradaOrigem() {
        when(entityManager.find(Beneficio.class, 3L, LockModeType.OPTIMISTIC)).thenReturn(null);
        assertThrows(ContaNaoEncontradaException.class,
                () -> ejbService.realizarTransferencia(3L, 4L, new BigDecimal("100")));
    }

    @Test
    void deveLancarContaNaoEncontradaDestino() {
        when(entityManager.find(Beneficio.class, 3L, LockModeType.OPTIMISTIC)).thenReturn(origem);
        when(entityManager.find(Beneficio.class, 4L, LockModeType.OPTIMISTIC)).thenReturn(null);
        assertThrows(ContaNaoEncontradaException.class,
                () -> ejbService.realizarTransferencia(3L, 4L, new BigDecimal("100")));
    }

    
    @Test
    void deveLancarMesmoIdOrigemEDestino() {
        assertThrows(TransferenciaNaoPermitidaException.class,
                () -> ejbService.realizarTransferencia(3L, 3L, new BigDecimal("100")));
    }


    @Test
    void deveLancarValorNulo() {
        assertThrows(TransferenciaNaoPermitidaException.class,
                () -> ejbService.realizarTransferencia(3L, 4L, null));
    }

    @Test
    void deveLancarValorNegativo() {
        assertThrows(TransferenciaNaoPermitidaException.class,
                () -> ejbService.realizarTransferencia(3L, 4L, new BigDecimal("-10")));
    }

    
    @Test
    void deveLancarIdOrigemNulo() {
        assertThrows(TransferenciaNaoPermitidaException.class,
                () -> ejbService.realizarTransferencia(null, 3L, new BigDecimal("100")));
    }

    @Test
    void deveLancarIdDestinoNulo() {
        assertThrows(TransferenciaNaoPermitidaException.class,
                () -> ejbService.realizarTransferencia(4L, null, new BigDecimal("100")));
    }

    @Test
    void deveLancarContaOrigemInativa() {
        origem.setAtiva(false);
        when(entityManager.find(Beneficio.class, 3L, LockModeType.OPTIMISTIC)).thenReturn(origem);
        when(entityManager.find(Beneficio.class, 4L, LockModeType.OPTIMISTIC)).thenReturn(destino);

        assertThrows(IllegalStateException.class,
                () -> ejbService.realizarTransferencia(3L, 4L, new BigDecimal("100")));
    }

    @Test
    void deveLancarContaDestinoInativa() {
        destino.setAtiva(false);
        when(entityManager.find(Beneficio.class, 3L, LockModeType.OPTIMISTIC)).thenReturn(origem);
        when(entityManager.find(Beneficio.class, 4L, LockModeType.OPTIMISTIC)).thenReturn(destino);

        assertThrows(IllegalStateException.class,
                () -> ejbService.realizarTransferencia(3L, 4L, new BigDecimal("100")));
    }

 
    @Test
    void deveLancarConcorrenciaDetectada() {
        when(entityManager.find(Beneficio.class, 3L, LockModeType.OPTIMISTIC)).thenReturn(origem);
        when(entityManager.find(Beneficio.class, 4L, LockModeType.OPTIMISTIC)).thenReturn(destino);

        
        doThrow(new OptimisticLockException()).when(entityManager).flush();

        assertThrows(ConcorrenciaDetectadaException.class,
                () -> ejbService.realizarTransferencia(3L, 4L, new BigDecimal("100")));
    }
}
