package com.luigarah.exception;

/**
 * Exceção lançada quando o usuário tenta criar conta tradicional com email já cadastrado via OAuth.
 */
public class ContaOAuthExistenteException extends RuntimeException {

    private final String provider;

    public ContaOAuthExistenteException(String message, String provider) {
        super(message);
        this.provider = provider;
    }

    public String getProvider() {
        return provider;
    }
}

