package com.example.ejb.exception;

public class TransferenciaNaoPermitidaException extends RuntimeException {
    public TransferenciaNaoPermitidaException(String msg) { super(msg); }
}