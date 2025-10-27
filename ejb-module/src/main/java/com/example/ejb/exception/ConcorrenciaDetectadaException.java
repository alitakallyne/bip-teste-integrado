package com.example.ejb.exception;

public class ConcorrenciaDetectadaException extends RuntimeException {
    public ConcorrenciaDetectadaException(String msg, Throwable cause) { super(msg, cause); }
}
