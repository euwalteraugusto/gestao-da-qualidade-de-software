package br.edu.iqs.aula06;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Atividade 3 (testes de "vida real") e desafio: senha nula, com 7 e com 8 caracteres,
 * organizados com {@code @Nested}.
 */
@DisplayName("UsuarioService")
class UsuarioServiceTest {

    private UsuarioService service;

    @BeforeEach
    void criarServico() {
        service = new UsuarioService();
    }

    @Nested
    @DisplayName("validarSenha")
    class ValidacaoSenhaTest {

        @Test
        @DisplayName("senha nula é inválida")
        void senhaNula() {
            assertFalse(service.validarSenha(null));
        }

        @Test
        @DisplayName("senha vazia é inválida")
        void senhaVazia() {
            assertFalse(service.validarSenha(""));
        }

        @Test
        @DisplayName("senha com 7 caracteres é inválida (logo abaixo do limite)")
        void senhaCom7Caracteres() {
            assertFalse(service.validarSenha("abcdefg"));
        }

        @Test
        @DisplayName("senha com 8 caracteres é válida (exatamente no limite)")
        void senhaCom8Caracteres() {
            assertTrue(service.validarSenha("abcdefgh"));
        }

        @Test
        @DisplayName("senha longa é válida")
        void senhaLonga() {
            assertTrue(service.validarSenha("uma-senha-bem-comprida-123"));
        }

        @ParameterizedTest(name = "\"{0}\" é inválida")
        @NullAndEmptySource
        @ValueSource(strings = {" ", "1234567", "abc", "       "})
        @DisplayName("várias senhas curtas/nulas/vazias são inválidas")
        void senhasInvalidas(String senha) {
            assertFalse(service.validarSenha(senha));
        }

        @ParameterizedTest(name = "\"{0}\" é válida")
        @ValueSource(strings = {"12345678", "senhaforte", "        "})
        @DisplayName("senhas com 8+ caracteres são válidas (o serviço só mede o tamanho)")
        void senhasValidas(String senha) {
            // Observacao: 8 espacos tambem passam, pois a regra e apenas length() >= 8.
            assertTrue(service.validarSenha(senha));
        }
    }
}
