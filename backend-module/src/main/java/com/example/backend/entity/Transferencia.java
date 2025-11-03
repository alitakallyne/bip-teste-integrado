package com.example.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.ejb.Beneficio;

@Entity
@Table(name = "TRANSFERENCIA")
public class Transferencia {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Benefício de origem é obrigatório")
    @ManyToOne
    @JoinColumn(name = "idOrigem", nullable = false)
    private Beneficio origem;

    @NotNull(message = "Benefício de destino é obrigatório")
    @ManyToOne
    @JoinColumn(name = "idDestino", nullable = false)
    private Beneficio destino;

    @NotNull(message = "O valor da transferência é obrigatório")
    @DecimalMin(value = "0.01", message = "O valor deve ser maior que zero")
    @Digits(integer = 13, fraction = 2, message = "O valor deve ter no máximo 13 dígitos inteiros e 2 decimais")
    private BigDecimal valor;

    private LocalDateTime dtTransferencia = LocalDateTime.now();

    @Size(max = 20)
    private String status = "SUCESSO";

    @Size(max = 64)
    @Column(unique = true)
    private String idempotencyKey;

    @Size(max = 255)
    private String mensagem;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Beneficio getOrigem() {
		return origem;
	}

	public void setOrigem(Beneficio origem) {
		this.origem = origem;
	}

	public Beneficio getDestino() {
		return destino;
	}

	public void setDestino(Beneficio destino) {
		this.destino = destino;
	}

	public BigDecimal getValor() {
		return valor;
	}

	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}

	public LocalDateTime getDtTransferencia() {
		return dtTransferencia;
	}

	public void setDtTransferencia(LocalDateTime dtTransferencia) {
		this.dtTransferencia = dtTransferencia;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getIdempotencyKey() {
		return idempotencyKey;
	}

	public void setIdempotencyKey(String idempotencyKey) {
		this.idempotencyKey = idempotencyKey;
	}

	public String getMensagem() {
		return mensagem;
	}

	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}
    
   
}
