package com.example.projeto.auth;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Le o header {@code Authorization: Bearer <token>}, valida o access token e popula
 * o {@code SecurityContext}.
 * <p>
 * Nao e um {@code @Component} de proposito: o Spring Boot registraria qualquer bean
 * {@code Filter} tambem como filtro global do servlet, executando-o duas vezes e fora
 * da cadeia de seguranca. Ele e instanciado em {@code SecurityConfig}.
 * Token presente porem invalido/expirado responde 401 imediatamente (em vez de
 * seguir anonimo e devolver um 403 enganoso).
 */
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final String PREFIXO = "Bearer ";

    private final JwtService jwtService;
    private final AuthenticationEntryPoint entryPoint;

    public JwtAuthFilter(JwtService jwtService, AuthenticationEntryPoint entryPoint) {
        this.jwtService = jwtService;
        this.entryPoint = entryPoint;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String token = extrairToken(request);
        if (token != null) {
            try {
                Claims claims = jwtService.validar(token, JwtService.TIPO_ACCESS);
                var auth = new UsernamePasswordAuthenticationToken(
                        jwtService.extrairUsername(claims), null, jwtService.extrairRoles(claims));
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContext contexto = SecurityContextHolder.createEmptyContext();
                contexto.setAuthentication(auth);
                SecurityContextHolder.setContext(contexto);
            } catch (TokenInvalidoException e) {
                SecurityContextHolder.clearContext();
                entryPoint.commence(request, response, new InsufficientAuthenticationException(e.getMessage(), e));
                return;
            }
        }
        chain.doFilter(request, response);
    }

    static String extrairToken(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null && header.startsWith(PREFIXO) && header.length() > PREFIXO.length()) {
            return header.substring(PREFIXO.length()).trim();
        }
        return null;
    }
}
