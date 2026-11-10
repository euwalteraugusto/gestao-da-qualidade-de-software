package com.example.projeto.auth;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Usuarios em memoria ({@code app.security.*}). Somente hashes BCrypt: as senhas em
 * texto puro de desenvolvimento estao documentadas em {@code docs/auth-guide.md};
 * em producao os hashes vem de variaveis de ambiente.
 */
@Validated
@ConfigurationProperties(prefix = "app.security")
public record UsuariosProperties(@Valid @NotNull Usuario admin, @Valid @NotNull Usuario cliente) {

    public record Usuario(@NotBlank String username, @NotBlank String passwordHash) {
    }
}
