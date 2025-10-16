package com.luigarah.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Configuração da Oracle Wallet - DESABILITADA
 * A extração da wallet agora é feita pelo DotEnvConfig antes da inicialização do Spring
 */
@Configuration
@Profile("disabled-oracle-wallet") // Profile que nunca será ativado
@Slf4j
public class OracleWalletConfig {
    // Esta classe está desabilitada pois a wallet agora é extraída pelo DotEnvConfig
}
