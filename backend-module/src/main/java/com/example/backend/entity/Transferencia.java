package com.example.backend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "TRANSFERENCIA")
public class Transferencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ID_ORIGEM", nullable = false)
    private Long idOrigem;

    @Column(name = "ID_DESTINO", nullable = false)
    private Long idDestino;

    @Column(nullable = false)
    private BigDecimal valor;

    @Column(name = "STATUS", length = 20, nullable = false)
    private String status = "SUCESSO";

    @Column(name = "IDEMPOTENCY_KEY", length = 64, unique = true)
    private String idempotencyKey;

    @Column(name = "DT_TRANSFERENCIA")
    private LocalDateTime dataTransferencia = LocalDateTime.now();

    // Getters e setters
    public Long getId() { return id; }
    public Long getIdOrigem() { return idOrigem; }
    public void setIdOrigem(Long idOrigem) { this.idOrigem = idOrigem; }
    public Long getIdDestino() { return idDestino; }
    public void setIdDestino(Long idDestino) { this.idDestino = idDestino; }
    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getIdempotencyKey() { return idempotencyKey; }
    public void setIdempotencyKey(String idempotencyKey) { this.idempotencyKey = idempotencyKey; }
}
