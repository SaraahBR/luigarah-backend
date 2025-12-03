package com.luigarah.exception;

/**
 * Exceção lançada quando credenciais de login são inválidas.
 */
public class CredenciaisInvalidasException extends RuntimeException {
    public CredenciaisInvalidasException(String message) {
        super(message);
    }
}

