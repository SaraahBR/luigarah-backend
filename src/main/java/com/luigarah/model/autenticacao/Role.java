package com.luigarah.model.autenticacao;

/**
 * Enum de roles (funções) de usuário.
 *
 * <ul>
 *   <li><b>USER</b> - Usuário comum (pode ver produtos, gerenciar carrinho e lista de desejos)</li>
 *   <li><b>ADMIN</b> - Administrador (acesso total ao sistema)</li>
 * </ul>
 */
public enum Role {
    /**
     * Usuário comum - pode navegar, adicionar ao carrinho e lista de desejos
     */
    USER,

    /**
     * Administrador - acesso completo a todas as funcionalidades
     */
    ADMIN
}
