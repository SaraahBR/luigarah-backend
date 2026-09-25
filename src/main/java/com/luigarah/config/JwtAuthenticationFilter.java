package com.luigarah.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

/**
 * Filtro JWT que intercepta todas as requisições e valida o token.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;
    private final UserDetailsService userDetailsService;

    /**
     * Leituras públicas do catálogo não precisam do usuário: sem este atalho, cada GET
     * de produto de quem está logado ia ao banco buscar a conta (Render/Oregon ->
     * Supabase/São Paulo) antes mesmo de chegar ao cache do catálogo.
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String metodo = request.getMethod();
        if ("OPTIONS".equalsIgnoreCase(metodo)) return true; // preflight CORS
        if (!"GET".equalsIgnoreCase(metodo) && !"HEAD".equalsIgnoreCase(metodo)) return false;
        String caminho = request.getRequestURI().substring(request.getContextPath().length());
        return Arrays.stream(SecurityConfig.CATALOGO)
                .map(p -> p.substring(0, p.length() - "/**".length()))
                .anyMatch(p -> caminho.equals(p) || caminho.startsWith(p + "/"));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwt = getJwtFromRequest(request);

            if (StringUtils.hasText(jwt)) {
                if (tokenProvider.validateToken(jwt)) {
                    String username = tokenProvider.getUsernameFromToken(jwt);
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    // Conta desativada pelo admin perde o acesso na hora, não só quando o token expira
                    if (userDetails.isEnabled() && userDetails.isAccountNonLocked()) {
                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        log.debug("JWT válido: {} {} ({})", request.getMethod(), request.getRequestURI(),
                                userDetails.getAuthorities());
                    } else {
                        log.info("JWT de conta desativada recusado: {} {}", request.getMethod(), request.getRequestURI());
                    }
                } else {
                    log.debug("JWT inválido ou expirado: {} {}", request.getMethod(), request.getRequestURI());
                }
            }
        } catch (Exception ex) {
            log.warn("Erro ao processar JWT em {}: {}", request.getRequestURI(), ex.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Extrai o token JWT do header Authorization.
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
