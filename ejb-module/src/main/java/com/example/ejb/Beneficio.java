package com.example.ejb;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

@Entity
@Table(name = "beneficio")
public class Beneficio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome", nullable = false, length = 100)
    private String nome;

    @Column(name = "descricao", length = 255)
    private String descricao;

    @Column(name = "saldo", nullable = false, precision = 15, scale = 2)
    private BigDecimal saldo = BigDecimal.ZERO;

    @Column(name = "ativo", nullable = false)
    private boolean ativa = true;

    @Column(name = "dt_criacao", updatable = false)
    private LocalDateTime dtCriacao = LocalDateTime.now();

    @Column(name = "dt_atualizacao")
    private LocalDateTime dtAtualizacao = LocalDateTime.now();

    @Version
    @Column(name = "version", nullable = false)
    private Long versao = 0L;

   
    public Beneficio(String string, String string2, BigDecimal bigDecimal) {
		
	}

	public Beneficio() {
		// TODO Auto-generated constructor stub
	}



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

    
    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getDescricao() { return descricao; }
    public BigDecimal getSaldo() { return saldo; }
    public boolean isAtiva() { return ativa; }
    public LocalDateTime getDtCriacao() { return dtCriacao; }
    public LocalDateTime getDtAtualizacao() { return dtAtualizacao; }
    public Long getVersao() { return versao; }

    public void setId(Long id) { this.id = id; }
    public void setNome(String nome) { this.nome = nome; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public void setSaldo(BigDecimal saldo) { this.saldo = saldo; }
    public void setAtiva(boolean ativa) { this.ativa = ativa; }
    public void setDtCriacao(LocalDateTime dtCriacao) { this.dtCriacao = dtCriacao; }
    public void setDtAtualizacao(LocalDateTime dtAtualizacao) { this.dtAtualizacao = dtAtualizacao; }
    public void setVersao(Long versao) { this.versao = versao; }

    @Override
    public String toString() {
        return String.format("Beneficio{id=%d, nome='%s', descricao='%s', saldo=%s, ativa=%s, versao=%d}", 
                id, nome, descricao, saldo, ativa, versao);
    }
}
