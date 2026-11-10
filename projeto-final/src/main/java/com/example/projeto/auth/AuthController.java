package com.example.projeto.auth;

import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoints publicos de autenticacao: login e renovacao de tokens (refresh).
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    public record LoginRequest(@NotBlank String username, @NotBlank String password) {
    }

    public record RefreshRequest(@NotBlank String refreshToken) {
    }

    public record TokenResponse(String accessToken, String refreshToken, String tokenType, long expiresIn) {
    }

    /** POST /auth/login - troca usuario/senha por access token + refresh token. */
    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody LoginRequest request) {
        var autenticacao = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        return emitir((UserDetails) autenticacao.getPrincipal());
    }

    /**
     * POST /auth/refresh (tarefa pos-aula 18) - valida o refresh token e emite um novo par
     * (rotacao: o cliente passa a usar o novo refresh token). Os papeis sao recarregados
     * do {@link UserDetailsService}, entao mudancas de permissao valem no proximo refresh.
     */
    @PostMapping("/refresh")
    public TokenResponse refresh(@Valid @RequestBody RefreshRequest request) {
        Claims claims = jwtService.validar(request.refreshToken(), JwtService.TIPO_REFRESH);
        UserDetails usuario = userDetailsService.loadUserByUsername(jwtService.extrairUsername(claims));
        return emitir(usuario);
    }

    private TokenResponse emitir(UserDetails usuario) {
        return new TokenResponse(jwtService.gerarAccessToken(usuario), jwtService.gerarRefreshToken(usuario),
                "Bearer", jwtService.validadeAccessToken().toSeconds());
    }

    @ExceptionHandler(AuthenticationException.class)
    public ProblemDetail credenciaisInvalidas() {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, "Usuario ou senha invalidos");
        pd.setTitle("Nao autenticado");
        return pd;
    }

    @ExceptionHandler(TokenInvalidoException.class)
    public ProblemDetail tokenInvalido(TokenInvalidoException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, ex.getMessage());
        pd.setTitle("Nao autenticado");
        return pd;
    }
}
