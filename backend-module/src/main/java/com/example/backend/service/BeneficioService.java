// service/BeneficioService.java
package com.example.backend.service;

import com.example.backend.dto.*;
import com.example.backend.exception.BusinessException;
import com.example.backend.mapper.BeneficioMapper;
import com.example.beneficioejb.service.BeneficioEjbService;
import jakarta.ejb.EJB;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class BeneficioService {

    @EJB
    private BeneficioEjbService beneficioEjbService;

    public List<BeneficioDTO> listarTodos() {
        return beneficioEjbService.listarTodos()
                .stream()
                .map(BeneficioMapper::toDTO)
                .toList();
    }

    public BeneficioDTO criar(BeneficioCreateRequest request) {
        var entity = BeneficioMapper.toEntity(request);
        var salvo = beneficioEjbService.salvar(entity);
        return BeneficioMapper.toDTO(salvo);
    }

    public TransferenciaResponse transferir(TransferenciaRequest request) {
        try {
            beneficioEjbService.transferir(
                    request.idOrigem(),
                    request.idDestino(),
                    request.valor(),
                    request.idempotencyKey()
            );
            return new TransferenciaResponse(
                    request.idOrigem(),
                    request.idDestino(),
                    request.valor(),
                    "Transferência realizada com sucesso!"
            );
        } catch (Exception e) {
            throw new BusinessException("Erro ao processar transferência: " + e.getMessage());
        }
    }
}
