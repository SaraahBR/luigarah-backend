package com.luigarah.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Utilitário para gerar hash BCrypt de senhas.
 * Use esta classe para gerar hashes de senha quando necessário.
 */
public class GerarHashSenha {

    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        String senhaOriginal = "Admin@123";
        String hashGerado = encoder.encode(senhaOriginal);

        System.out.println("=".repeat(60));
        System.out.println("GERADOR DE HASH BCRYPT");
        System.out.println("=".repeat(60));
        System.out.println("Senha original: " + senhaOriginal);
        System.out.println("Hash BCrypt gerado:");
        System.out.println(hashGerado);
        System.out.println("=".repeat(60));

        // Testar se o hash funciona
        String hashAtual = "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy";
        boolean senhaCorreta = encoder.matches(senhaOriginal, hashAtual);

        System.out.println("\nTeste de validação:");
        System.out.println("Hash atual no banco: " + hashAtual);
        System.out.println("Senha 'Admin@123' corresponde ao hash? " + (senhaCorreta ? "SIM ✓" : "NÃO ✗"));
        System.out.println("=".repeat(60));

        if (!senhaCorreta) {
            System.out.println("\n⚠️  ATENÇÃO: O hash atual NÃO corresponde à senha!");
            System.out.println("Use o hash gerado acima para atualizar no banco de dados.");
        }
    }
}

