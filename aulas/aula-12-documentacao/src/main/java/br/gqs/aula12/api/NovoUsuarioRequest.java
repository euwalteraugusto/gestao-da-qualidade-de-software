package br.gqs.aula12.api;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Corpo da requisição de criação de usuário.
 *
 * @param nome nome completo (obrigatório, até 100 caracteres)
 * @param email e-mail válido (obrigatório)
 */
@Schema(description = "Dados para criar um usuário")
public record NovoUsuarioRequest(
        @Schema(description = "Nome completo", example = "Ana Souza", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank @Size(max = 100) String nome,
        @Schema(description = "E-mail válido", example = "ana@exemplo.com", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank @Email String email) {
}
