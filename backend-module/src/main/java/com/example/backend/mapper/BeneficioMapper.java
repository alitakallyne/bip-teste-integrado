
package com.example.backend.mapper;

import com.example.backend.dto.BeneficioResponse;
import com.example.backend.dto.BeneficioRequest;
import com.example.ejb.*;

public class BeneficioMapper {

    public static BeneficioResponse toResponse(Beneficio entity) {
        return new BeneficioResponse(
                entity.getId(),
                entity.getNome(),
                entity.getDescricao(),
                entity.getSaldo(),
                entity.isAtiva()
        );
    }

    public static Beneficio toEntity(BeneficioRequest request) {
        Beneficio entity = new Beneficio();
        entity.setNome(request.nome());
        entity.setDescricao(request.descricao());
        entity.setSaldo(request.saldo());
        entity.setAtiva(true);
        return entity;
    }
    
    
}
