package br.edu.iqs.aula06;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Amostra de asserções fluentes com AssertJ (recurso citado no slide), lado a lado com o JUnit. */
@DisplayName("AssertJ (asserções fluentes)")
class AssertJTest {

    @Test
    void somaComAssertJ() {
        assertThat(Calculadora.somar(2, 3)).isEqualTo(5).isPositive();
    }

    @Test
    void divisaoComTolerancia() {
        assertThat(Calculadora.dividir(9, 4)).isCloseTo(2.25, within(1e-9));
    }

    @Test
    void excecaoComMensagem() {
        assertThatThrownBy(() -> Calculadora.dividir(1, 0))
                .isInstanceOf(DivisaoPorZeroException.class)
                .isInstanceOf(ArithmeticException.class)
                .hasMessage("Divisão por zero não permitida");
    }

    @Test
    void stringsEmVariasCondicoes() {
        assertThat(StringUtils.inverterString("abc"))
                .isEqualTo("cba")
                .hasSize(3)
                .startsWith("c");
    }
}
