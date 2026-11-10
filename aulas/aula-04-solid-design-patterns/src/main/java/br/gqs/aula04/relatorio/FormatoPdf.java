package br.gqs.aula04.relatorio;

/** Formato PDF (simulado como texto, pois o foco da aula é o padrão de projeto). */
public final class FormatoPdf implements FormatoRelatorio {

    @Override
    public String gerar(DadosRelatorio dados) {
        StringBuilder sb = new StringBuilder("%PDF-SIMULADO\n").append(dados.titulo()).append('\n');
        for (String linha : dados.linhas()) {
            sb.append("- ").append(linha).append('\n');
        }
        return sb.toString();
    }
}
