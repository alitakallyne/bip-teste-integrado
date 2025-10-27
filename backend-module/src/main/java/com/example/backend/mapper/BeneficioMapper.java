
package com.example.backend.mapper;

import com.example.backend.dto.BeneficioDTO;
import com.example.backend.dto.BeneficioCreateRequest;
import com.example.beneficioejb.entity.Beneficio; // importando a entidade do módulo EJB

public class BeneficioMapper {

    public static BeneficioDTO toDTO(Beneficio entity) {
        return new BeneficioDTO(
                entity.getId(),
                entity.getNome(),
                entity.getDescricao(),
                entity.getValor(),
                entity.getAtivo()
        );
    }

    public static Beneficio toEntity(BeneficioCreateRequest request) {
        Beneficio entity = new Beneficio();
        entity.setNome(request.nome());
        entity.setDescricao(request.descricao());
        entity.setValor(request.valor());
        entity.setAtivo(true);
        return entity;
    }
}
