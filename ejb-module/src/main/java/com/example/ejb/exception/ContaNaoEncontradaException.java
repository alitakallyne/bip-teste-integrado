package com.example.ejb.exception;

public class ContaNaoEncontradaException extends RuntimeException {
    public ContaNaoEncontradaException(Long id) { super("Conta não encontrada: ID=" + id); }
}