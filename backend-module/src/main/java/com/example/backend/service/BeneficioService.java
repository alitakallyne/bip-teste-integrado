package com.example.backend.service;

import com.example.backend.dto.*;
import com.example.backend.mapper.BeneficioMapper;
import com.example.backend.repository.BeneficioRepository;
import com.example.ejb.Beneficio;
import com.example.ejb.BeneficioEjbService;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class BeneficioService {

  
	@Autowired
    private BeneficioEjbService beneficioEjbService;

    @Autowired
    private BeneficioRepository beneficioRepository;
    


    public List<BeneficioResponse> getAllBeneficios() {
        return beneficioRepository.findAll()
                .stream()
                .map(BeneficioMapper::toResponse)
                .toList();
    }

    public BeneficioResponse getBeneficioById(Long id) {
        Beneficio beneficio = beneficioRepository.findByIdAndAtivaTrue(id)
                .orElseThrow(() -> new IllegalArgumentException("Benefício não encontrado"));
        return BeneficioMapper.toResponse(beneficio);
    }

    @Transactional
    public BeneficioResponse createBeneficio(BeneficioRequest request) {
        Beneficio entity = BeneficioMapper.toEntity(request);
        Beneficio saved = beneficioRepository.save(entity);
        return BeneficioMapper.toResponse(saved);
    }

    @Transactional
    public BeneficioResponse updateBeneficio(Long id, BeneficioRequest request) {
        Beneficio beneficio = beneficioRepository.findByIdAndAtivaTrue(id)
                .orElseThrow(() -> new IllegalArgumentException("Benefício não encontrado"));
        beneficio.setNome(request.nome());
        beneficio.setDescricao(request.descricao());
        beneficio.setSaldo(request.saldo());
        Beneficio updated = beneficioRepository.save(beneficio);
        return BeneficioMapper.toResponse(updated);
    }

    @Transactional
    public void deactivateBeneficio(Long id) {
        Beneficio beneficio = beneficioRepository.findByIdAndAtivaTrue(id)
                .orElseThrow(() -> new IllegalArgumentException("Benefício não encontrado"));
        beneficio.setAtiva(false);
        beneficioRepository.save(beneficio);
    }

    @Transactional
    public void transferirValor(TransferenciaRequest request) {
    	System.out.println("Recebido TransferenciaRequest: {}"+request);
        beneficioEjbService.realizarTransferencia(               
                request.idOrigem(),
                request.idDestino(),
                request.valor()
        );
    }
}
