package com.example.backend.mapper;

import com.example.backend.dto.TransferenciaRequest;
import com.example.backend.dto.TransferenciaResponse;
import com.example.backend.entity.Transferencia;
import com.example.ejb.Beneficio;

public class TransferenciaMapper {
	 public static TransferenciaResponse toResponse(Transferencia entity) {
	        return new TransferenciaResponse(
	                entity.getOrigem().getId(),
	                entity.getDestino().getId(),
	                entity.getValor(),
	                entity.getMensagem()
	        );
	    }

	 public static Transferencia toEntity(TransferenciaRequest request) {
	        Transferencia entity = new Transferencia();
	        entity.setValor(request.valor());
	        entity.setIdempotencyKey(request.idempotencyKey());
	        entity.setMensagem("Transferência processada");

	        
	        Beneficio origem = new Beneficio();
	        origem.setId(request.idOrigem());
	        entity.setOrigem(origem);

	        Beneficio destino = new Beneficio();
	        destino.setId(request.idDestino());
	        entity.setDestino(destino);

	        return entity;
	    }
}
