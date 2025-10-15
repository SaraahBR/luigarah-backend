package com.luigarah.model.autenticacao;

/**
 * Enum de provedores de autenticação.
 *
 * <ul>
 *   <li><b>LOCAL</b> - Autenticação tradicional com email/senha</li>
 *   <li><b>GOOGLE</b> - Autenticação via Google OAuth2</li>
 *   <li><b>FACEBOOK</b> - Autenticação via Facebook OAuth2</li>
 * </ul>
 */
public enum AuthProvider {
    /**
     * Autenticação local com email e senha
     */
    LOCAL,

    /**
     * Autenticação via Google OAuth2
     */
    GOOGLE,

    /**
     * Autenticação via Facebook OAuth2
     */
    FACEBOOK
}
