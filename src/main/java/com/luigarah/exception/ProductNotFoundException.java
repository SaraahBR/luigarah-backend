package com.luigarah.exception;

public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException(String mensagem) {
        super(mensagem);
    }

    public ProductNotFoundException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}