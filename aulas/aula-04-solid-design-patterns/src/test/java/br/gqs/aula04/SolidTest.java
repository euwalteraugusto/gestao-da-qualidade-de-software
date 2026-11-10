package br.gqs.aula04;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.gqs.aula04.solid.Ave;
import br.gqs.aula04.solid.Logavel;
import br.gqs.aula04.solid.Pardal;
import br.gqs.aula04.solid.Pinguim;
import br.gqs.aula04.solid.Salvavel;
import br.gqs.aula04.solid.Usuario;
import br.gqs.aula04.solid.UsuarioRepository;
import br.gqs.aula04.solid.Voador;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

/** Demonstrações de LSP, ISP e SRP/DIP (os demais princípios estão nos outros testes). */
class SolidTest {

    @Test
    void lspQualquerAveSubstituiAIntencaoBase() {
        List<Ave> aves = List.of(new Pardal(), new Pinguim());
        for (Ave ave : aves) {
            assertTrue(ave.comer().endsWith("está comendo"));
        }
    }

    @Test
    void lspSoQuemVoaImplementaVoador() {
        List<Ave> aves = List.of(new Pardal(), new Pinguim());
        List<String> voos = new ArrayList<>();
        for (Ave ave : aves) {
            if (ave instanceof Voador voador) {
                voos.add(voador.voar());
            }
        }
        assertEquals(List.of("Pardal está voando"), voos);
        // Pinguim é final e não implementa Voador: nem compila "new Pinguim() instanceof Voador".
        Ave pinguim = new Pinguim();
        assertFalse(pinguim instanceof Voador);
    }

    @Test
    void ispUsuarioImplementaInterfacesPequenas() {
        Usuario usuario = new Usuario("Ana");
        Logavel logavel = usuario;
        Salvavel salvavel = usuario;
        assertEquals("Usuario[Ana]", logavel.mensagemDeLog());
        List<String> destino = new ArrayList<>();
        salvavel.salvarEm(destino);
        assertEquals(List.of("Ana"), destino);
    }

    @Test
    void srpRepositorioPersisteEUsuarioSoRepresentaOsDados() {
        UsuarioRepository repository = new UsuarioRepository();
        repository.salvar(new Usuario("Ana"));
        repository.salvar(new Usuario("Bia"));
        assertEquals(List.of("Ana", "Bia"), repository.listar());
        assertThrows(UnsupportedOperationException.class, () -> repository.listar().add("x"));
    }

    @Test
    void usuarioValidaNomeEEncapsula() {
        assertThrows(IllegalArgumentException.class, () -> new Usuario(" "));
        assertThrows(IllegalArgumentException.class, () -> new Usuario(null));
        assertEquals("Ana", new Usuario("Ana").getNome());
        assertThrows(NullPointerException.class, () -> new Usuario("Ana").salvarEm(null));
    }
}

