package com.luigarah.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exceção lançada quando há violação de regra de negócio.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class RegraDeNegocioException extends RuntimeException {

    public RegraDeNegocioException(String message) {
        super(message);
    }

    public RegraDeNegocioException(String message, Throwable cause) {
        super(message, cause);
    }
}

