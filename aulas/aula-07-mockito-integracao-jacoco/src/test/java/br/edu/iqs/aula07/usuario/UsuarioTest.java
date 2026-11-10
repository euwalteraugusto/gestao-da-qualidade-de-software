package br.edu.iqs.aula07.usuario;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Cobre equals/hashCode/toString/id de {@link Usuario} (apareciam sem cobertura no JaCoCo). */
@DisplayName("Usuario")
class UsuarioTest {

    @Test
    void novoUsuarioNaoTemId() {
        Usuario u = new Usuario("Ana");
        assertNull(u.getId());
        assertEquals("Ana", u.getNome());
    }

    @Test
    void igualdadePorIdENome() {
        Usuario a = new Usuario("Ana");
        a.setId(1L);
        Usuario b = new Usuario("Ana");
        b.setId(1L);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertEquals(a, a);

        b.setId(2L);
        assertNotEquals(a, b);
        assertNotEquals(a, new Usuario("Outra"));
        assertNotEquals(a, null);
        assertNotEquals(a, "Ana");
    }

    @Test
    void toStringMostraIdENome() {
        Usuario u = new Usuario("Ana");
        u.setId(5L);
        assertTrue(u.toString().contains("5") && u.toString().contains("Ana"), u.toString());
    }
}
