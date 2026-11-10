package br.gqs.aula04.relatorio;

import java.util.List;

/**
 * Conteúdo de um relatório (imutável).
 *
 * @param titulo título, obrigatório
 * @param linhas linhas do corpo; {@code null} vira lista vazia
 */
public record DadosRelatorio(String titulo, List<String> linhas) {

    /** Valida o título e faz cópia defensiva das linhas. */
    public DadosRelatorio {
        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException("Título é obrigatório.");
        }
        linhas = linhas == null ? List.of() : List.copyOf(linhas);
    }
}
