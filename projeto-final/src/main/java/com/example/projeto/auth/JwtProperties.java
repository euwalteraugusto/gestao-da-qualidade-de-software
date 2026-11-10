package com.example.projeto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

/**
 * Configuracao do JWT lida de {@code app.jwt.*}.
 * <p>
 * A chave NUNCA fica no codigo (o slide tinha {@code SECRET_KEY = "mySecretKey123!"}):
 * em producao vem da variavel de ambiente {@code JWT_SECRET}; o perfil {@code dev}
 * (padrao local e dos testes) define uma chave de desenvolvimento.
 *
 * @param secret            chave HMAC (minimo 32 bytes / 256 bits para HS256)
 * @param issuer            emissor gravado e exigido nos tokens
 * @param accessExpiration  validade do access token
 * @param refreshExpiration validade do refresh token
 */
@Validated
@ConfigurationProperties(prefix = "app.jwt")
public record JwtProperties(
        @NotBlank(message = "app.jwt.secret (JWT_SECRET) e obrigatorio") String secret,
        @NotBlank String issuer,
        @NotNull Duration accessExpiration,
        @NotNull Duration refreshExpiration) {
}
