package com.luigarah.exception;

/**
 * Exceção lançada quando a senha não atende aos requisitos de validação
 */
public class SenhaInvalidaException extends RuntimeException {

    public SenhaInvalidaException(String message) {
        super(message);
    }
}

