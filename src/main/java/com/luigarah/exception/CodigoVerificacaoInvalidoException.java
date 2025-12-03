package com.luigarah.exception;

/**
 * Exceção lançada quando um código de verificação é inválido, expirado ou já foi usado
 */
public class CodigoVerificacaoInvalidoException extends RuntimeException {

    private final String tipoErro;

    public CodigoVerificacaoInvalidoException(String message, String tipoErro) {
        super(message);
        this.tipoErro = tipoErro;
    }

    public String getTipoErro() {
        return tipoErro;
    }
}

