package br.gqs.aula11.depois;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/**
 * DEPOIS da refatoração: sem ifs aninhados, sem concatenação em laço, formatos isolados em
 * {@link FormatoRelatorio} e dinheiro em BigDecimal.
 *
 * <p>Mudança de comportamento documentada: formato desconhecido agora falha rápido com
 * {@link IllegalArgumentException} (antes devolvia texto sem formatação nem rodapé).</p>
 */
public class RelatorioVendas {

    /**
     * Gera o relatório das vendas com valor positivo (nulos e valores não positivos são ignorados).
     *
     * @param vendas vendas a listar
     * @param formato CSV, TXT ou HTML
     * @return texto do relatório, terminando no rodapé com o total
     * @throws IllegalArgumentException se o formato for nulo ou desconhecido
     */
    public String gerar(List<Venda> vendas, String formato) {
        FormatoRelatorio saida = FormatoRelatorio.de(formato);
        List<Venda> validas = vendas.stream()
                .filter(Objects::nonNull)
                .filter(venda -> venda.valor().signum() > 0)
                .toList();
        BigDecimal total = validas.stream()
                .map(Venda::valor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        StringBuilder relatorio = new StringBuilder();
        validas.forEach(venda -> relatorio.append(saida.linha(venda)));
        return relatorio.append(saida.rodape(total)).toString();
    }
}
