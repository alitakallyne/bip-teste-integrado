package com.example.ejb.exception;

public class SaldoInsuficienteException extends RuntimeException {
    public SaldoInsuficienteException(String msg) { super(msg); }
}