package com.luigarah.exception;

/**
 * Exceção lançada quando o usuário tenta fazer login sem ter verificado o email.
 */
public class ContaNaoVerificadaException extends RuntimeException {
    public ContaNaoVerificadaException(String message) {
        super(message);
    }
}

