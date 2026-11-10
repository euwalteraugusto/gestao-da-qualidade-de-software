package br.edu.iqs.aula06;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

/** Atividade 2: testes parametrizados (varios inputs sem repetir codigo). */
@DisplayName("Calculadora - testes parametrizados")
class CalculadoraParametrizadaTest {

    private static final double DELTA = 1e-9;

    // ---- @MethodSource: exatamente o exemplo do slide ------------------------------------

    @ParameterizedTest(name = "{0} + {1} = {2}")
    @MethodSource("geradorDeTestes")
    @DisplayName("Soma com @MethodSource")
    void testSomaComParametros(int a, int b, int esperado) {
        assertEquals(esperado, Calculadora.somar(a, b));
    }

    private static Stream<Arguments> geradorDeTestes() {
        return Stream.of(
                Arguments.of(1, 1, 2),     // caso positivo
                Arguments.of(-1, -1, -2),  // caso negativo
                Arguments.of(0, 5, 5),     // caso com zero
                Arguments.of(Integer.MAX_VALUE, 0, Integer.MAX_VALUE) // limite
        );
    }

    // ---- Exercicio: dividir com (4,2,2) e (9,4,2.25) -------------------------------------

    @ParameterizedTest(name = "{0} / {1} = {2}")
    @CsvSource({
            "4, 2, 2.0",
            "9, 4, 2.25",
            "-9, 4, -2.25",
            "9, -4, -2.25",
            "0, 7, 0.0",
            "1, 3, 0.3333333333333333"
    })
    @DisplayName("Divisão com @CsvSource")
    void testDividirComParametros(int a, int b, double esperado) {
        assertEquals(esperado, Calculadora.dividir(a, b), DELTA);
    }

    @ParameterizedTest(name = "{0} / 0 lança DivisaoPorZeroException")
    @ValueSource(ints = {1, 0, -7, Integer.MAX_VALUE, Integer.MIN_VALUE})
    @DisplayName("Divisão por zero com @ValueSource")
    void dividirPorZeroParaVariosDividendos(int dividendo) {
        assertThrows(DivisaoPorZeroException.class, () -> Calculadora.dividir(dividendo, 0));
    }

    @ParameterizedTest(name = "somarExato({0}, {1}) estoura")
    @CsvSource({
            "2147483647, 1",
            "-2147483648, -1",
            "2147483647, 2147483647"
    })
    @DisplayName("somarExato detecta estouro")
    void somarExatoEstoura(int a, int b) {
        assertThrows(ArithmeticException.class, () -> Calculadora.somarExato(a, b));
    }
}
