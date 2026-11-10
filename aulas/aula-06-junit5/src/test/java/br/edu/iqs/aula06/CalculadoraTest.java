package br.edu.iqs.aula06;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.TimeUnit;

/**
 * Atividade 1: primeiros testes unitarios. Segue a anatomia do slide
 * (@BeforeAll, @BeforeEach, @Test, @DisplayName, @AfterEach) e resolve os exercicios
 * (numeros negativos e limite com Integer.MAX_VALUE).
 */
@DisplayName("Calculadora - testes unitarios basicos")
class CalculadoraTest {

    private static int testesIniciados;
    private int execucoesDoBeforeEach;

    @BeforeAll
    static void setup() {
        // Inicializacao global (uma vez). Aqui apenas zera o contador didatico.
        testesIniciados = 0;
    }

    @BeforeEach
    void init() {
        // Pre-teste: roda antes de CADA teste, em uma instancia nova da classe.
        testesIniciados++;
        execucoesDoBeforeEach++;
    }

    @AfterEach
    void tearDown() {
        // Pos-teste: liberaria recursos (arquivos, conexoes). Nada a limpar aqui.
        assertEquals(1, execucoesDoBeforeEach, "cada teste usa uma instancia nova da classe");
    }

    @AfterAll
    static void encerramento() {
        assertTrue(testesIniciados > 0, "pelo menos um teste deveria ter rodado");
    }

    @Test
    @DisplayName("Soma de 2 + 2 deve ser 4")
    void testSoma() {
        assertEquals(4, Calculadora.somar(2, 2));
    }

    @Test
    @DisplayName("Divisão por zero deve lançar exceção")
    void testDivisaoPorZero() {
        assertThrows(DivisaoPorZeroException.class, () -> Calculadora.dividir(1, 0));
    }

    @Test
    @DisplayName("Divisão por zero informa a mensagem esperada")
    void testMensagemDaDivisaoPorZero() {
        DivisaoPorZeroException e = assertThrows(DivisaoPorZeroException.class,
                () -> Calculadora.dividir(10, 0));
        assertEquals("Divisão por zero não permitida", e.getMessage());
    }

    // ---- Exercicio: soma com numeros negativos -------------------------------------------

    @Test
    @DisplayName("Soma de dois negativos")
    void somaDeNegativos() {
        assertEquals(-5, Calculadora.somar(-2, -3));
    }

    @Test
    @DisplayName("Soma de positivo com negativo (resultado negativo, zero e positivo)")
    void somaMista() {
        assertAll("sinais mistos",
                () -> assertEquals(-1, Calculadora.somar(1, -2)),
                () -> assertEquals(0, Calculadora.somar(5, -5)),
                () -> assertEquals(3, Calculadora.somar(-2, 5)));
    }

    // ---- Exercicio: limite com Integer.MAX_VALUE -----------------------------------------

    @Test
    @DisplayName("Soma no limite: MAX_VALUE + 0 e MIN_VALUE + MAX_VALUE não estouram")
    void somaNoLimiteSemEstouro() {
        assertAll(
                () -> assertEquals(Integer.MAX_VALUE, Calculadora.somar(Integer.MAX_VALUE, 0)),
                () -> assertEquals(-1, Calculadora.somar(Integer.MIN_VALUE, Integer.MAX_VALUE)));
    }

    @Test
    @DisplayName("Soma estoura: MAX_VALUE + 1 'dá a volta' para MIN_VALUE (comportamento documentado)")
    void somaEstouraDaVolta() {
        // Teste de caracterizacao: registra o comportamento atual, que e um risco conhecido.
        assertEquals(Integer.MIN_VALUE, Calculadora.somar(Integer.MAX_VALUE, 1));
    }

    @Test
    @DisplayName("somarExato lança ArithmeticException no estouro")
    void somarExatoDetectaEstouro() {
        assertThrows(ArithmeticException.class, () -> Calculadora.somarExato(Integer.MAX_VALUE, 1));
        assertThrows(ArithmeticException.class, () -> Calculadora.somarExato(Integer.MIN_VALUE, -1));
        assertEquals(Integer.MAX_VALUE, Calculadora.somarExato(Integer.MAX_VALUE - 1, 1));
    }

    // ---- Outras anotacoes da tabela do slide ---------------------------------------------

    @Test
    @Disabled("Demonstração de @Disabled: teste desabilitado temporariamente (aparece como Skipped)")
    @DisplayName("Teste desabilitado de propósito")
    void testeDesabilitado() {
        assertEquals(5, Calculadora.somar(2, 2)); // falharia se rodasse
    }

    @RepeatedTest(value = 3, name = "repetição {currentRepetition} de {totalRepetitions}")
    @DisplayName("Soma é determinística (repetida 3 vezes)")
    void somaRepetida() {
        assertEquals(7, Calculadora.somar(3, 4));
    }

    @Test
    @Timeout(value = 1, unit = TimeUnit.SECONDS)
    @Tag("rapido")
    @DisplayName("Operações simples devem ser rápidas (@Timeout de 1 segundo)")
    void operacaoRapida() {
        assertEquals(2.5, Calculadora.dividir(5, 2), 1e-9);
        org.junit.jupiter.api.Assertions.assertTimeout(Duration.ofMillis(500),
                () -> Calculadora.somar(1, 1));
    }
}
