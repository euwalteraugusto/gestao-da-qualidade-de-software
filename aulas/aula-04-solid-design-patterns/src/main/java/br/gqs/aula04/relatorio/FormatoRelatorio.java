package br.gqs.aula04.relatorio;

/**
 * Strategy: contrato comum dos formatos de relatório.
 *
 * <p>Novos formatos (CSV, Markdown...) são novas implementações, sem alterar
 * as classes existentes (OCP).
 */
public interface FormatoRelatorio {

    /**
     * Gera o relatório no formato desta estratégia.
     *
     * @param dados conteúdo do relatório
     * @return texto do relatório
     */
    String gerar(DadosRelatorio dados);
}
