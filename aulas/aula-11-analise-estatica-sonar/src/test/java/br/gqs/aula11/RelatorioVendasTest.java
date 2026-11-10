package br.gqs.aula11;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/** Caracterização do relatório: mesmas entradas, mesma saída nas duas versões. */
class RelatorioVendasTest {

    private final br.gqs.aula11.antes.RelatorioVendas antes = new br.gqs.aula11.antes.RelatorioVendas();
    private final br.gqs.aula11.depois.RelatorioVendas depois = new br.gqs.aula11.depois.RelatorioVendas();

    private String gerarAntes(String formato) {
        return antes.gerar(Arrays.asList(
                new br.gqs.aula11.antes.Venda("Ana", 100.5),
                null,
                new br.gqs.aula11.antes.Venda("Bia", -3),
                new br.gqs.aula11.antes.Venda("Caio", 50.5)), formato);
    }

    private String gerarDepois(String formato) {
        return depois.gerar(Arrays.asList(
                new br.gqs.aula11.depois.Venda("Ana", new BigDecimal("100.5")),
                null,
                new br.gqs.aula11.depois.Venda("Bia", new BigDecimal("-3")),
                new br.gqs.aula11.depois.Venda("Caio", new BigDecimal("50.5"))), formato);
    }

    @ParameterizedTest
    @ValueSource(strings = {"CSV", "TXT", "HTML"})
    void duasVersoesGeramAMesmaSaida(String formato) {
        assertEquals(gerarAntes(formato), gerarDepois(formato));
    }

    @Test
    void formatoCsvTemLinhasERodape() {
        assertEquals("Ana;100.5\nCaio;50.5\nTOTAL;151.0\n", gerarDepois("CSV"));
    }

    @Test
    void formatoTxtETextoSimples() {
        assertEquals("Cliente: Ana - R$ 100.5\nCliente: Caio - R$ 50.5\nTotal geral: R$ 151.0\n", gerarDepois("TXT"));
    }

    @Test
    void formatoHtmlGeraLinhasDeTabela() {
        assertEquals("<tr><td>Ana</td><td>100.5</td></tr>\n<tr><td>Caio</td><td>50.5</td></tr>\n"
                + "<tr><td>TOTAL</td><td>151.0</td></tr>\n", gerarDepois("HTML"));
    }

    @Test
    void listaVaziaGeraApenasORodape() {
        assertEquals("TOTAL;0\n", depois.gerar(List.of(), "CSV"));
    }

    @Test
    void formatoDesconhecidoOuNuloFalhaRapidoNaVersaoDepois() {
        assertThrows(IllegalArgumentException.class, () -> depois.gerar(List.of(), "PDF"));
        assertThrows(IllegalArgumentException.class, () -> depois.gerar(List.of(), null));
    }

    @Test
    void versaoAntesIgnoravaFormatoDesconhecidoSemAvisar() {
        assertEquals("", antes.gerar(List.of(new br.gqs.aula11.antes.Venda("Ana", 1)), "PDF"));
    }

    @Test
    void versaoAntesPermiteMutacaoDaVenda() {
        br.gqs.aula11.antes.Venda venda = new br.gqs.aula11.antes.Venda("Ana", 1);
        venda.setCliente("Zeca");
        venda.setValor(2);
        assertEquals("Zeca;2.0\nTOTAL;2.0\n", antes.gerar(List.of(venda), "CSV"));
    }
}
