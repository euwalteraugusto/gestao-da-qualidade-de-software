package br.gqs.aula04.relatorio;

/**
 * Formato HTML. Diferente da versão "antes", escapa {@code & < >} para evitar
 * injeção de HTML a partir do conteúdo do relatório.
 */
public final class FormatoHtml implements FormatoRelatorio {

    @Override
    public String gerar(DadosRelatorio dados) {
        StringBuilder sb = new StringBuilder("<html><body><h1>")
                .append(escapar(dados.titulo())).append("</h1><ul>");
        for (String linha : dados.linhas()) {
            sb.append("<li>").append(escapar(linha)).append("</li>");
        }
        return sb.append("</ul></body></html>").toString();
    }

    private static String escapar(String texto) {
        return texto.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
