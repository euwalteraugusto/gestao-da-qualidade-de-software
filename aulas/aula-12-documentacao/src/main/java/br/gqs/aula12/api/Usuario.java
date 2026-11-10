package br.gqs.aula12.api;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Usuário cadastrado.
 *
 * @param id identificador gerado pelo servidor
 * @param nome nome completo
 * @param email e-mail de contato
 */
@Schema(description = "Usuário cadastrado")
public record Usuario(
        @Schema(description = "Identificador gerado pelo servidor", example = "1") Long id,
        @Schema(description = "Nome completo", example = "Ana Souza") String nome,
        @Schema(description = "E-mail de contato", example = "ana@exemplo.com") String email) {
}
