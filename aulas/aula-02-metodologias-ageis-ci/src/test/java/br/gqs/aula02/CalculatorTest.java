package br.gqs.aula02;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Testes unitários da classe {@link Calculator}.
 * Executados automaticamente pelo pipeline de CI a cada push/pull request.
 */
class CalculatorTest {

    /** Teste do slide: 2 + 2 = 4. */
    @Test
    void testAdd() {
        assertEquals(4, Calculator.add(2, 2));
    }

    /** Teste do desafio do slide: 3 - 2 = 1. */
    @Test
    void testSubtract() {
        assertEquals(1, Calculator.subtract(3, 2));
    }

    @ParameterizedTest(name = "{0} + {1} = {2}")
    @CsvSource({"0,0,0", "-1,1,0", "-5,-7,-12", "100,250,350", "2147483646,1,2147483647"})
    void somaVariosCenarios(int a, int b, int esperado) {
        assertEquals(esperado, Calculator.add(a, b));
    }

    @ParameterizedTest(name = "{0} - {1} = {2}")
    @CsvSource({"0,0,0", "2,3,-1", "-5,-7,2", "10,10,0", "-2147483647,1,-2147483648"})
    void subtraiVariosCenarios(int a, int b, int esperado) {
        assertEquals(esperado, Calculator.subtract(a, b));
    }

    @Test
    void somaComOverflowLancaArithmeticException() {
        assertThrows(ArithmeticException.class, () -> Calculator.add(Integer.MAX_VALUE, 1));
        assertThrows(ArithmeticException.class, () -> Calculator.add(Integer.MIN_VALUE, -1));
    }

    @Test
    void subtracaoComOverflowLancaArithmeticException() {
        assertThrows(ArithmeticException.class, () -> Calculator.subtract(Integer.MIN_VALUE, 1));
        assertThrows(ArithmeticException.class, () -> Calculator.subtract(Integer.MAX_VALUE, -1));
    }
}
