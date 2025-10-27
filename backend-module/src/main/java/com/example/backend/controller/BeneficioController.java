// controller/BeneficioController.java
package com.example.backend.controller;

import com.example.backend.dto.*;
import com.example.backend.service.BeneficioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/beneficios")
public class BeneficioController {

    private final BeneficioService beneficioService;

    public BeneficioController(BeneficioService beneficioService) {
        this.beneficioService = beneficioService;
    }

    @GetMapping
    public ResponseEntity<List<BeneficioDTO>> listar() {
        return ResponseEntity.ok(beneficioService.listarTodos());
    }

    @PostMapping
    public ResponseEntity<BeneficioDTO> criar(@RequestBody @Valid BeneficioCreateRequest request) {
        return ResponseEntity.ok(beneficioService.criar(request));
    }

    @PostMapping("/transferencia")
    public ResponseEntity<TransferenciaResponse> transferir(@RequestBody @Valid TransferenciaRequest request) {
        return ResponseEntity.ok(beneficioService.transferir(request));
    }
}
