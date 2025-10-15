package com.luigarah.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuração de segurança do Spring Security com JWT.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configure(http))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Endpoints públicos
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/oauth2/**").permitAll()

                        // Swagger/OpenAPI público
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()

                        // Home público
                        .requestMatchers("/", "/home").permitAll()

                        // Produtos - leitura pública
                        .requestMatchers(HttpMethod.GET, "/api/produtos/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/identidades/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/tamanhos/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/padroes-tamanho/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/estoque/**").permitAll()

                        // Produtos - escrita apenas ADMIN
                        .requestMatchers(HttpMethod.POST, "/api/produtos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/produtos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/produtos/**").hasRole("ADMIN")

                        // Identidades - escrita apenas ADMIN
                        .requestMatchers(HttpMethod.POST, "/api/identidades/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/identidades/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/identidades/**").hasRole("ADMIN")

                        // Tamanhos - escrita apenas ADMIN
                        .requestMatchers(HttpMethod.POST, "/api/tamanhos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/tamanhos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/tamanhos/**").hasRole("ADMIN")

                        // Estoque - escrita apenas ADMIN
                        .requestMatchers(HttpMethod.POST, "/api/estoque/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/estoque/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/estoque/**").hasRole("ADMIN")

                        // Carrinho e Lista de Desejos - apenas usuários autenticados
                        .requestMatchers("/api/carrinho/**").authenticated()
                        .requestMatchers("/api/lista-desejos/**").authenticated()

                        // Perfil de usuário - apenas usuários autenticados
                        .requestMatchers("/api/usuario/**").authenticated()

                        // Todos os outros endpoints requerem autenticação
                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
