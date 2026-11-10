package br.edu.iqs.aula06;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Desafio Avancado: refatoracao do teste com 3 asserts em sequencia para
 * {@code @ParameterizedTest} + {@code assertAll}.
 * <p>
 * Problema do teste original: se o primeiro assertEquals falha, os outros dois nem executam, e o
 * relatorio nao diz qual dos casos (1+2, 1+(-2), 0+0) quebrou.
 * <p>
 * Problema da "solucao sugerida" do slide: o segundo assert dentro do assertAll era
 * {@code assertTrue(a + b == esperado)}, que apenas repete a conta com o mesmo operador {@code +}
 * do Java (nao testa o codigo do projeto e passaria mesmo se Calculadora.somar estivesse errada).
 * Aqui ele foi trocado por uma verificacao real e independente: a comutatividade da soma.
 */
class DesafioAvancadoTest {

    @ParameterizedTest(name = "somar({0}, {1}) = {2}")
    @CsvSource({"1, 2, 3", "1, -2, -1", "0, 0, 0", "-3, -4, -7", "2147483647, 0, 2147483647"})
    @DisplayName("Soma parametrizada com assertAll (resultado e comutatividade)")
    void testSomaParametrizada(int a, int b, int esperado) {
        assertAll(
                () -> assertEquals(esperado, Calculadora.somar(a, b), "a + b"),
                () -> assertEquals(esperado, Calculadora.somar(b, a), "b + a (comutatividade)"));
    }
}
