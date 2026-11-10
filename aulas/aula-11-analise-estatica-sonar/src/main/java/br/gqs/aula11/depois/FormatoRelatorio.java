package br.gqs.aula11.depois;

import java.math.BigDecimal;

/** Formatos de saída do relatório: cada um sabe formatar uma linha e o rodapé. */
public enum FormatoRelatorio {

    /** Valores separados por ponto e vírgula. */
    CSV {
        @Override
        String linha(Venda venda) {
            return venda.cliente() + ";" + venda.valor().toPlainString() + FIM_LINHA;
        }

        @Override
        String rodape(BigDecimal total) {
            return "TOTAL;" + total.toPlainString() + FIM_LINHA;
        }
    },

    /** Texto simples. */
    TXT {
        @Override
        String linha(Venda venda) {
            return "Cliente: " + venda.cliente() + " - R$ " + venda.valor().toPlainString() + FIM_LINHA;
        }

        @Override
        String rodape(BigDecimal total) {
            return "Total geral: R$ " + total.toPlainString() + FIM_LINHA;
        }
    },

    /** Linhas de tabela HTML. */
    HTML {
        @Override
        String linha(Venda venda) {
            return "<tr><td>" + venda.cliente() + "</td><td>" + venda.valor().toPlainString() + FECHA_LINHA_HTML;
        }

        @Override
        String rodape(BigDecimal total) {
            return "<tr><td>TOTAL</td><td>" + total.toPlainString() + FECHA_LINHA_HTML;
        }
    };

    private static final String FIM_LINHA = "\n";
    private static final String FECHA_LINHA_HTML = "</td></tr>" + FIM_LINHA;

    abstract String linha(Venda venda);

    abstract String rodape(BigDecimal total);

    /**
     * Converte o nome do formato.
     *
     * @param nome CSV, TXT ou HTML (sensível a maiúsculas, como no código original)
     * @return o formato correspondente
     * @throws IllegalArgumentException se o nome for nulo ou desconhecido
     */
    public static FormatoRelatorio de(String nome) {
        if (nome == null) {
            throw new IllegalArgumentException("Formato é obrigatório");
        }
        try {
            return valueOf(nome);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Formato desconhecido: " + nome, e);
        }
    }
}
