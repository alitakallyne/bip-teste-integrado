// dto/TransferenciaResponse.java
package com.example.backend.dto;

import java.math.BigDecimal;

public record TransferenciaResponse(
        Long idOrigem,
        Long idDestino,
        BigDecimal valorTransferido,
        String mensagem
) {}
