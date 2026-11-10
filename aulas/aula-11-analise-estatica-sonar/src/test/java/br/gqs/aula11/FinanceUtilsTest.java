package br.gqs.aula11;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.function.BiFunction;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Testes de caracterização: o mesmo conjunto de casos roda contra a versão ANTES e a DEPOIS,
 * provando que a refatoração preservou o comportamento (exceto o bug do tipo nulo, tratado à parte).
 */
class FinanceUtilsTest {

    private static final BiFunction<BigDecimal, String, BigDecimal> ANTES =
            new br.gqs.aula11.antes.FinanceUtils()::calcularImposto;
    private static final BiFunction<BigDecimal, String, BigDecimal> DEPOIS =
            new br.gqs.aula11.depois.FinanceUtils()::calcularImposto;

    static Stream<Arguments> casos() {
        return Stream.of(
                Arguments.of("ICMS", "1000", "170.00"),
                Arguments.of("IPI", "10000", "500.00"),
                Arguments.of("IPI", "10000.01", "1000.00"),
                Arguments.of("IPI", "100", "5.00"),
                Arguments.of("ISS", "200", "10.00"),
                Arguments.of("PIS", "1000", "16.50"),
                Arguments.of("COFINS", "5000", "150.00"),
                Arguments.of("COFINS", "5000.01", "380.00"),
                Arguments.of("COFINS", "100", "3.00"),
                Arguments.of("ICMS", "0.01", "0.00"));
    }

    @ParameterizedTest(name = "{0} sobre {1} = {2}")
    @MethodSource("casos")
    void versaoAntesCalculaCorretamente(String tipo, String valor, String esperado) {
        assertEquals(new BigDecimal(esperado), ANTES.apply(new BigDecimal(valor), tipo));
    }

    @ParameterizedTest(name = "{0} sobre {1} = {2}")
    @MethodSource("casos")
    void versaoDepoisTemOMesmoResultado(String tipo, String valor, String esperado) {
        assertEquals(new BigDecimal(esperado), DEPOIS.apply(new BigDecimal(valor), tipo));
    }

    @Test
    void valorNuloEhZeroNasDuasVersoes() {
        assertEquals(BigDecimal.ZERO, ANTES.apply(null, "ICMS"));
        assertEquals(BigDecimal.ZERO, DEPOIS.apply(null, "ICMS"));
    }

    @Test
    void tipoDesconhecidoEhZeroNasDuasVersoes() {
        assertEquals(BigDecimal.ZERO, ANTES.apply(BigDecimal.TEN, "XYZ"));
        assertEquals(BigDecimal.ZERO, DEPOIS.apply(BigDecimal.TEN, "XYZ"));
    }

    @Test
    void tipoNuloEraBugNaVersaoAntesEFoiCorrigido() {
        assertThrows(NullPointerException.class, () -> ANTES.apply(BigDecimal.TEN, null));
        assertEquals(BigDecimal.ZERO, DEPOIS.apply(BigDecimal.TEN, null));
    }

    @Test
    void versaoAntesRegistraUltimoErro() {
        br.gqs.aula11.antes.FinanceUtils antes = new br.gqs.aula11.antes.FinanceUtils();
        antes.calcularImposto(BigDecimal.TEN, "XYZ");
        assertEquals("tipo de imposto desconhecido: XYZ", antes.ultimoErro);
    }
}
