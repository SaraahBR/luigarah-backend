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
@EnableMethodSecurity(prePostEnabled = true)
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
                        // ✅ Endpoints públicos de autenticação
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/oauth2/**").permitAll()

                        // ✅ Swagger/OpenAPI público
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()

                        // ✅ Home público
                        .requestMatchers("/", "/home").permitAll()

                        // ✅ PRODUTOS - Leitura pública (QUALQUER PESSOA PODE VER)
                        .requestMatchers(HttpMethod.GET, "/api/produtos/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/produtos/**").permitAll()

                        // ✅ CATEGORIAS - Leitura pública (CORREÇÃO DO ERRO 403)
                        .requestMatchers(HttpMethod.GET, "/api/categorias/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/categorias/**").permitAll()

                        // ✅ MARCAS - Leitura pública
                        .requestMatchers(HttpMethod.GET, "/api/marcas/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/marcas/**").permitAll()

                        // ✅ IDENTIDADES - Leitura pública
                        .requestMatchers(HttpMethod.GET, "/api/identidades/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/identidades/**").permitAll()

                        // ✅ TAMANHOS - Leitura pública
                        .requestMatchers(HttpMethod.GET, "/api/tamanhos/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/tamanhos/**").permitAll()

                        // ✅ PADRÕES DE TAMANHO - Leitura pública
                        .requestMatchers(HttpMethod.GET, "/api/padroes-tamanho/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/padroes-tamanho/**").permitAll()

                        // ✅ ESTOQUE - Leitura pública
                        .requestMatchers(HttpMethod.GET, "/api/estoque/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/estoque/**").permitAll()

                        // ✅ BUSCA - Leitura pública
                        .requestMatchers(HttpMethod.GET, "/api/busca/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/busca/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/search/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/search/**").permitAll()

                        // 🔒 PRODUTOS - Escrita apenas ADMIN
                        .requestMatchers(HttpMethod.POST, "/api/produtos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/produtos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/produtos/**").hasRole("ADMIN")

                        // 🔒 IDENTIDADES - Escrita apenas ADMIN
                        .requestMatchers(HttpMethod.POST, "/api/identidades/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/identidades/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/identidades/**").hasRole("ADMIN")

                        // 🔒 TAMANHOS - Escrita apenas ADMIN
                        .requestMatchers(HttpMethod.POST, "/api/tamanhos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/tamanhos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/tamanhos/**").hasRole("ADMIN")

                        // 🔒 ESTOQUE - Escrita apenas ADMIN
                        .requestMatchers(HttpMethod.POST, "/api/estoque/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/estoque/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/estoque/**").hasRole("ADMIN")

                        // 🔒 CARRINHO - Apenas usuários autenticados
                        .requestMatchers("/api/carrinho/**").authenticated()
                        .requestMatchers("/carrinho/**").authenticated()

                        // 🔒 LISTA DE DESEJOS - Apenas usuários autenticados
                        .requestMatchers("/api/lista-desejos/**").authenticated()
                        .requestMatchers("/lista-desejos/**").authenticated()
                        .requestMatchers("/api/favoritos/**").authenticated()
                        .requestMatchers("/favoritos/**").authenticated()

                        // 🔒 PEDIDOS - Apenas usuários autenticados
                        .requestMatchers("/api/pedidos/**").authenticated()
                        .requestMatchers("/pedidos/**").authenticated()

                        // 🔒 PERFIL - Apenas usuários autenticados
                        .requestMatchers("/api/usuario/**").authenticated()

                        // 🔒 ADMIN - Apenas ADMIN
                        .requestMatchers("/api/admin/**").authenticated()

                        // 🔒 Todos os outros endpoints requerem autenticação
                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
