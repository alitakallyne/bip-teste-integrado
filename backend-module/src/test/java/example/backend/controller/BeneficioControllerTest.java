package example.backend.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.result.JsonPathResultMatchers;
import org.springframework.test.web.servlet.result.StatusResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.example.backend.controller.BeneficioController;
import com.example.backend.dto.BeneficioRequest;
import com.example.backend.dto.BeneficioResponse;
import com.example.backend.dto.TransferenciaRequest;
import com.example.backend.service.BeneficioService;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class BeneficioControllerTest {

    @InjectMocks
    private BeneficioController controller;

    @Mock
    private BeneficioService service;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    private BeneficioRequest request;
    private BeneficioResponse response;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        request = new BeneficioRequest("Vale Alimentação", "Benefício de alimentação", new BigDecimal("1500"));
        response = new BeneficioResponse(1L, "Vale Alimentação", "Benefício de alimentação", new BigDecimal("1500"), true);
    }

    @Test
    void deveCriarBeneficioComSucesso() throws Exception {
        when(service.createBeneficio(any(BeneficioRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/beneficios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(((JsonPathResultMatchers) jsonPath("$.nome")).value("Vale Alimentação"));

        verify(service, times(1)).createBeneficio(any(BeneficioRequest.class));
    }

    @Test
    void deveListarBeneficios() throws Exception {
        when(service.getAllBeneficios()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/v1/beneficios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].nome").value("Vale Alimentação"));

        verify(service, times(1)).getAllBeneficios();
    }


	@Test
    void deveTransferirValor() throws Exception {
        TransferenciaRequest tr = new TransferenciaRequest(1L, 2L, new BigDecimal("100"), null);

        mockMvc.perform(post("/api/v1/beneficios/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(tr)))
                .andExpect(((StatusResultMatchers) status()).isOk());

        verify(service, times(1)).transferirValor(tr);
    }

}
