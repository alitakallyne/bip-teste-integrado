
package com.example.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record BeneficioRequest(
        @NotBlank String nome,
        String descricao,
        @NotNull BigDecimal saldo
) {}
