package example.backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.backend.dto.BeneficioRequest;
import com.example.backend.dto.BeneficioResponse;
import com.example.backend.dto.TransferenciaRequest;
import com.example.backend.repository.BeneficioRepository;
import com.example.backend.service.BeneficioService;
import com.example.ejb.Beneficio;
import com.example.ejb.BeneficioEjbService;

@ExtendWith(MockitoExtension.class)
class BeneficioServiceTest {

    @InjectMocks
    private BeneficioService beneficioService;

    @Mock
    private BeneficioRepository beneficioRepository;

    @Mock
    private BeneficioEjbService beneficioEjbService;

    private Beneficio beneficio;
    private BeneficioRequest request;

    @BeforeEach
    void setUp() {
        beneficio = new Beneficio();
        beneficio.setId(1L);
        beneficio.setNome("Vale Alimentação");
        beneficio.setDescricao("Benefício de alimentação");
        beneficio.setSaldo(new BigDecimal("1500"));
        beneficio.setAtiva(true);

        request = new BeneficioRequest("Vale Alimentação", "Benefício de alimentação", new BigDecimal("1500"));
    }

    @Test
    void deveCriarBeneficioComSucesso() {
        when(beneficioRepository.save(any(Beneficio.class))).thenReturn(beneficio);

        BeneficioResponse response = beneficioService.createBeneficio(request);

        verify(beneficioRepository, times(1)).save(any(Beneficio.class));
        assertThat(response.nome()).isEqualTo("Vale Alimentação");
    }

    @Test
    void deveAtualizarBeneficioComSucesso() {
        when(beneficioRepository.findByIdAndAtivaTrue(1L)).thenReturn(Optional.of(beneficio));
        when(beneficioRepository.save(beneficio)).thenReturn(beneficio);

        BeneficioResponse response = beneficioService.updateBeneficio(1L, request);

        verify(beneficioRepository).findByIdAndAtivaTrue(1L);
        verify(beneficioRepository).save(beneficio);
        assertThat(response.saldo()).isEqualTo(new BigDecimal("1500"));
    }

    @Test
    void deveDesativarBeneficio() {
        when(beneficioRepository.findByIdAndAtivaTrue(1L)).thenReturn(Optional.of(beneficio));

        beneficioService.deactivateBeneficio(1L);

        verify(beneficioRepository).save(beneficio);
        assertThat(beneficio.isAtiva()).isFalse();
    }

    @Test
    void deveTransferirValorEntreBeneficiosChamandoEJB() {
        TransferenciaRequest tr = new TransferenciaRequest(1L, 2L, new BigDecimal("100"), null);

        beneficioService.transferirValor(tr);

        verify(beneficioEjbService, times(1))
            .realizarTransferencia(1L, 2L, new BigDecimal("100"));
    }
}
