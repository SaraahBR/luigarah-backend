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

/**
 * Filtro JWT que intercepta todas as requisições e valida o token.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // ✅ CORREÇÃO CRÍTICA: Permitir requisições OPTIONS (CORS preflight) sem autenticação
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            log.debug("Requisição OPTIONS detectada - pulando validação JWT para URI: {}", request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String jwt = getJwtFromRequest(request);

            if (StringUtils.hasText(jwt)) {
                log.debug("Token JWT encontrado para URI: {}", request.getRequestURI());

                if (tokenProvider.validateToken(jwt)) {
                    String username = tokenProvider.getUsernameFromToken(jwt);
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    log.info("=== JWT FILTER - AUTENTICAÇÃO VÁLIDA ===");
                    log.info("Request URI: {}", request.getRequestURI());
                    log.info("Usuário: {}", username);
                    log.info("Authorities: {}", userDetails.getAuthorities());

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.info("Authentication setado com sucesso");
                    log.info("==========================================");
                } else {
                    log.error("Token JWT INVÁLIDO para URI: {} - Token será rejeitado", request.getRequestURI());
                    log.error("Token fornecido: {}", jwt.substring(0, Math.min(20, jwt.length())) + "...");
                }
            } else {
                log.debug("Nenhum token JWT fornecido para URI: {}", request.getRequestURI());
            }
        } catch (Exception ex) {
            log.error("Erro ao processar autenticação JWT para URI: {}", request.getRequestURI(), ex);
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
