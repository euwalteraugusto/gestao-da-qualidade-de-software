package br.gqs.aula09;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class CalculadoraTest {

    private final Calculadora calculadora = new Calculadora();

    @Test
    void dividePositivos() {
        assertEquals(3, calculadora.divide(9, 3));
    }

    @Test
    void divideComResultadoInteiroTruncado() {
        assertEquals(2, calculadora.divide(7, 3));
    }

    @Test
    void divisaoPorZeroLancaExcecaoComMensagemClara() {
        ArithmeticException ex = assertThrows(ArithmeticException.class, () -> calculadora.divide(1, 0));
        assertEquals("Divisor não pode ser zero", ex.getMessage());
    }
}
