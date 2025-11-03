
package com.example.backend.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TransferenciaRequest(
		@NotNull @JsonProperty("id_origem") Long idOrigem,
	    @NotNull @JsonProperty("id_destino") Long idDestino,
	    @NotNull BigDecimal valor,
	    String idempotencyKey
) {}
