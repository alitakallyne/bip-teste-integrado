// dto/TransferenciaRequest.java
package com.example.backend.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record TransferenciaRequest(
        @NotNull Long idOrigem,
        @NotNull Long idDestino,
        @NotNull BigDecimal valor,
        String idempotencyKey
) {}
