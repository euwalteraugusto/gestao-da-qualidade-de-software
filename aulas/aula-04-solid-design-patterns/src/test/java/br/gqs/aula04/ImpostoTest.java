package br.gqs.aula04;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import br.gqs.aula04.antes.CalculadoraImpostoAntes;
import br.gqs.aula04.imposto.CalculadoraImposto;
import br.gqs.aula04.imposto.Icms;
import br.gqs.aula04.imposto.ImpostoStrategy;
import br.gqs.aula04.imposto.Iss;
import br.gqs.aula04.imposto.NotaFiscalService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/** Desafio prático: Strategy para impostos (ICMS, ISS) e DIP. */
class ImpostoTest {

    private static final double DELTA = 1e-9;

    private final CalculadoraImposto calculadora = new CalculadoraImposto();

    @Test
    void antesCalculaIcmsIssEZeroParaDesconhecido() {
        CalculadoraImpostoAntes antes = new CalculadoraImpostoAntes();
        assertEquals(10.0, antes.calcular("ICMS", 100), DELTA);
        assertEquals(5.0, antes.calcular("ISS", 100), DELTA);
        assertEquals(0.0, antes.calcular("IPI", 100), DELTA);
    }

    @Test
    void depoisCalculaIcmsEIss() {
        assertEquals(10.0, calculadora.calcular(new Icms(), 100), DELTA);
        assertEquals(5.0, calculadora.calcular(new Iss(), 100), DELTA);
    }

    @ParameterizedTest
    @ValueSource(doubles = {0, 1, 99.99, 100, 12345.67})
    void depoisEquivaleAAntesParaIcmsEIss(double valor) {
        CalculadoraImpostoAntes antes = new CalculadoraImpostoAntes();
        assertEquals(antes.calcular("ICMS", valor), calculadora.calcular(new Icms(), valor), DELTA);
        assertEquals(antes.calcular("ISS", valor), calculadora.calcular(new Iss(), valor), DELTA);
    }

    @Test
    void novoImpostoSemAlterarAsClassesExistentes_ocp() {
        ImpostoStrategy ipi = valor -> valor * 0.15;
        assertEquals(15.0, calculadora.calcular(ipi, 100), DELTA);
    }

    @Test
    void rejeitaValoresInvalidosEImpostoNulo() {
        assertThrows(IllegalArgumentException.class, () -> calculadora.calcular(new Icms(), -1));
        assertThrows(IllegalArgumentException.class, () -> calculadora.calcular(new Icms(), Double.NaN));
        assertThrows(NullPointerException.class, () -> calculadora.calcular(null, 10));
    }

    @Test
    void notaFiscalDependeDaAbstracaoEAceitaImpostoFalso_dip() {
        ImpostoStrategy fixo = valor -> 7.0;
        NotaFiscalService service = new NotaFiscalService(calculadora, fixo);
        assertEquals(107.0, service.totalComImposto(100), DELTA);
    }

    @Test
    void notaFiscalComIcmsReal() {
        assertEquals(110.0, new NotaFiscalService(calculadora, new Icms()).totalComImposto(100), DELTA);
        assertEquals(105.0, new NotaFiscalService(calculadora, new Iss()).totalComImposto(100), DELTA);
    }

    @Test
    void notaFiscalValidaDependencias() {
        assertThrows(NullPointerException.class, () -> new NotaFiscalService(null, new Icms()));
        assertThrows(NullPointerException.class, () -> new NotaFiscalService(calculadora, null));
    }
}
