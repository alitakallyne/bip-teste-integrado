package com.example.ejb;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "beneficio")
public class Beneficio  {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private boolean ativa = true;
    private BigDecimal saldo;

    @Version
    private Long versao;

    // --- Métodos de domínio ---
    public void verificarAtiva() {
        if (!ativa) {
            throw new IllegalStateException("Conta inativa: " + nome);
        }
    }

    public void debitar(BigDecimal valor) {
        if (saldo.compareTo(valor) < 0) {
            throw new IllegalStateException("Saldo insuficiente para débito.");
        }
        saldo = saldo.subtract(valor);
    }

    public void creditar(BigDecimal valor) {
        saldo = saldo.add(valor);
    }

    // --- Getters e Setters ---
    public Long getId() { return id; }
    public String getNome() { return nome; }
    public BigDecimal getSaldo() { return saldo; }
    public boolean isAtiva() { return ativa; }
    public Long getVersao() { return versao; }

    public void setId(Long id) { this.id = id; }
    public void setNome(String nome) { this.nome = nome; }
    public void setSaldo(BigDecimal saldo) { this.saldo = saldo; }
    public void setAtiva(boolean ativa) { this.ativa = ativa; }

    @Override
    public String toString() {
        return String.format("ContaBeneficio{id=%d, nome='%s', saldo=%s, ativa=%s}", id, nome, saldo, ativa);
    }
}

