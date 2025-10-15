package com.luigarah;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordTest {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        String senhaDigitada = "Admin@123";
        String hashNoBanco = "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy";

        System.out.println("Senha digitada: " + senhaDigitada);
        System.out.println("Hash no banco: " + hashNoBanco);
        System.out.println("Tamanho do hash: " + hashNoBanco.length());
        System.out.println("Senha corresponde ao hash? " + encoder.matches(senhaDigitada, hashNoBanco));

        // Gerar novo hash para comparação
        String novoHash = encoder.encode(senhaDigitada);
        System.out.println("\nNovo hash gerado: " + novoHash);
        System.out.println("Novo hash corresponde? " + encoder.matches(senhaDigitada, novoHash));
    }
}

