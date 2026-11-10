package br.gqs.aula09;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class NotificacaoServiceTest {

    private final NotificacaoService service = new NotificacaoService();

    @ParameterizedTest
    @ValueSource(strings = {"ana@exemplo.com", "ana.silva+tag@mail.empresa.com.br", "a_b-c%d@d-1.org",
        "  ana@exemplo.com  "})
    void aceitaEmailsValidos(String email) {
        assertTrue(service.emailValido(email));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "semarroba.com", "@exemplo.com", "ana@", "ana@exemplo", "ana@exemplo.c",
        "ana silva@exemplo.com", "ana@@exemplo.com", "ana@.com"})
    void rejeitaEmailsInvalidos(String email) {
        assertFalse(service.emailValido(email));
    }

    @Test
    void enviaEmailValido() {
        String resposta = service.enviarEmail("ana@exemplo.com", "Olá", "Corpo");
        assertEquals("E-mail enviado para ana@exemplo.com com assunto 'Olá'", resposta);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"invalido", "x@y"})
    void enviarComDestinatarioInvalidoLancaExcecao(String destinatario) {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.enviarEmail(destinatario, "Olá", "Corpo"));
        assertTrue(ex.getMessage().startsWith("E-mail inválido"));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    void enviarSemAssuntoLancaExcecao(String assunto) {
        assertThrows(IllegalArgumentException.class,
                () -> service.enviarEmail("ana@exemplo.com", assunto, "Corpo"));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    void enviarSemCorpoLancaExcecao(String corpo) {
        assertThrows(IllegalArgumentException.class,
                () -> service.enviarEmail("ana@exemplo.com", "Olá", corpo));
    }
}
