
package com.example.backend.controller;

import com.example.backend.dto.*;
import com.example.backend.service.BeneficioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/beneficios")
@Tag(name = "Benefícios", description = "API para gerenciamento de benefícios")
public class BeneficioController {

    private final BeneficioService service;

    public BeneficioController(BeneficioService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar todos os benefícios",
               description = "Retorna a lista de todos os benefícios ativos no sistema")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso",
                     content = @Content(schema = @Schema (implementation = BeneficioResponse.class)))
    })
    public ResponseEntity<List<BeneficioResponse>> listAll() {
     
        List<BeneficioResponse> beneficios = service.getAllBeneficios();
        return ResponseEntity.ok(beneficios);
    }
    @GetMapping("/{id}")
    @Operation(summary = "Buscar benefício por ID",
               description = "Retorna os detalhes de um benefício específico")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Benefício encontrado",
                     content = @Content(schema = @Schema(implementation = BeneficioResponse.class))),
        @ApiResponse(responseCode = "404", description = "Benefício não encontrado")
    })
    public ResponseEntity<BeneficioResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getBeneficioById(id));
    }

    @PostMapping
    @Operation(summary = "Criar novo benefício",
               description = "Cria um novo benefício no sistema")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Benefício criado com sucesso",
                     content = @Content(schema = @Schema(implementation = BeneficioResponse.class))),
        @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    public ResponseEntity<BeneficioResponse> create(@Valid @RequestBody BeneficioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createBeneficio(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar benefício",
               description = "Atualiza os dados de um benefício existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Benefício atualizado com sucesso",
                     content = @Content(schema = @Schema(implementation = BeneficioResponse.class))),
        @ApiResponse(responseCode = "404", description = "Benefício não encontrado"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    public ResponseEntity<BeneficioResponse> update(@PathVariable Long id,
                                                    @Valid @RequestBody BeneficioRequest request) {
        return ResponseEntity.ok(service.updateBeneficio(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Desativar benefício",
               description = "Desativa um benefício (soft delete) - não remove do banco")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Benefício desativado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Benefício não encontrado")
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deactivateBeneficio(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/transfer")
    @Operation(summary = "Transferir valor entre benefícios",
               description = "Realiza transferência de valor de um benefício para outro de forma segura")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Transferência realizada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "404", description = "Benefício não encontrado"),
        @ApiResponse(responseCode = "409", description = "Conflito de concorrência"),
        @ApiResponse(responseCode = "422", description = "Saldo insuficiente")
    })
    public ResponseEntity<TransferenciaResponse> transfer(@Valid @RequestBody TransferenciaRequest request) {
        service.transferirValor(request);
        return ResponseEntity.ok().build();
    }
}

