package br.edu.iqs.aula06;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

/** Tarefa da Aula 6: testes para {@code StringUtils.isEmpty()} e {@code inverterString()}. */
@DisplayName("StringUtils")
class StringUtilsTest {

    @ParameterizedTest(name = "isEmpty({0}) = true")
    @NullAndEmptySource
    @DisplayName("isEmpty: nulo e vazio")
    void isEmptyVerdadeiro(String texto) {
        assertTrue(StringUtils.isEmpty(texto));
    }

    @ParameterizedTest(name = "isEmpty(\"{0}\") = false")
    @ValueSource(strings = {" ", "a", "texto", "\t", "0"})
    @DisplayName("isEmpty: espaços e textos NÃO são vazios")
    void isEmptyFalso(String texto) {
        assertFalse(StringUtils.isEmpty(texto));
    }

    @ParameterizedTest(name = "inverterString(\"{0}\") = \"{1}\"")
    @CsvSource({
            "abc, cba",
            "a, a",
            "ana, ana",
            "'Java 17', '71 avaJ'",
            "12345, 54321"
    })
    @DisplayName("inverterString: casos comuns")
    void inverterCasosComuns(String entrada, String esperado) {
        assertEquals(esperado, StringUtils.inverterString(entrada));
    }

    @Test
    @DisplayName("inverterString: vazio continua vazio e nulo continua nulo")
    void inverterVazioENulo() {
        assertEquals("", StringUtils.inverterString(""));
        assertNull(StringUtils.inverterString(null));
    }

    @Test
    @DisplayName("inverterString: acentos e emoji (par substituto) são preservados")
    void inverterUnicode() {
        assertEquals("álo", StringUtils.inverterString("olá"));
        // "a<emoji>b": o emoji ocupa 2 chars (par substituto) e nao pode ser quebrado ao meio
        String emoji = "😀";
        assertEquals("b" + emoji + "a", StringUtils.inverterString("a" + emoji + "b"));
    }

    @Test
    @DisplayName("inverter duas vezes devolve o original (propriedade)")
    void inverterDuasVezes() {
        String original = "Qualidade de Software";
        assertEquals(original, StringUtils.inverterString(StringUtils.inverterString(original)));
    }
}
