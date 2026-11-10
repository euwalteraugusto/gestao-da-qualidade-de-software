package br.edu.iqs.aula05.usuario;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class CadastroUsuarioTest {

    @Test
    @DisplayName("Cadastra usuário maior de idade")
    void cadastraMaiorDeIdade() {
        CadastroUsuario cadastro = new CadastroUsuario();
        Usuario u = cadastro.cadastrar("  Carla  ", 18);
        assertEquals(new Usuario("Carla", 18), u);
        assertEquals(List.of(u), cadastro.getCadastrados());
    }

    @Test
    @DisplayName("Menor de idade lança IdadeInvalidaException (em vez de System.err.println)")
    void menorDeIdade() {
        CadastroUsuario cadastro = new CadastroUsuario();
        IdadeInvalidaException e = assertThrows(IdadeInvalidaException.class,
                () -> cadastro.cadastrar("Davi", 17));
        assertEquals(17, e.getIdade());
        assertTrue(e.getMessage().contains("menor de idade"), e.getMessage());
        assertTrue(cadastro.getCadastrados().isEmpty(), "cadastro rejeitado não pode ser gravado");
    }

    @ParameterizedTest(name = "idade {0} é inválida")
    @ValueSource(ints = {-1, 131, 1000, Integer.MIN_VALUE})
    @DisplayName("Idades impossíveis são rejeitadas")
    void idadesImpossiveis(int idade) {
        CadastroUsuario cadastro = new CadastroUsuario();
        assertThrows(IdadeInvalidaException.class, () -> cadastro.cadastrar("X", idade));
    }

    @Test
    @DisplayName("Nome nulo ou em branco é rejeitado")
    void nomeInvalido() {
        CadastroUsuario cadastro = new CadastroUsuario();
        assertThrows(IllegalArgumentException.class, () -> cadastro.cadastrar(null, 20));
        assertThrows(IllegalArgumentException.class, () -> cadastro.cadastrar("   ", 20));
    }

    @Test
    @DisplayName("salvarArquivo grava os usuários e fecha o recurso (try-with-resources)")
    void salvaArquivo(@TempDir Path pasta) throws Exception {
        CadastroUsuario cadastro = new CadastroUsuario();
        cadastro.cadastrar("Carla", 30);
        cadastro.cadastrar("João", 45);
        Path arquivo = pasta.resolve("usuarios.txt");

        cadastro.salvarArquivo(arquivo.toString());

        List<String> linhas = Files.readAllLines(arquivo, StandardCharsets.UTF_8);
        assertEquals(List.of("Carla;30", "João;45"), linhas);
        // Se o FileWriter tivesse ficado aberto, o Windows nao deixaria apagar o arquivo.
        Files.delete(arquivo);
    }

    @Test
    @DisplayName("Falha de I/O vira ArquivoUsuariosException (checked) preservando a causa")
    void falhaDeIo(@TempDir Path pasta) {
        CadastroUsuario cadastro = new CadastroUsuario();
        // Passar um diretorio como arquivo provoca IOException.
        ArquivoUsuariosException e = assertThrows(ArquivoUsuariosException.class,
                () -> cadastro.salvarArquivo(pasta.toString()));
        assertNotNull(e.getCause());
        assertInstanceOf(IOException.class, e.getCause());
        assertTrue(e.getMessage().contains(pasta.toString()));
    }

    @Test
    @DisplayName("Hierarquia: IdadeInvalida é unchecked; ArquivoUsuarios é checked")
    void checkedVsUnchecked() {
        assertTrue(RuntimeException.class.isAssignableFrom(IdadeInvalidaException.class));
        assertTrue(Exception.class.isAssignableFrom(ArquivoUsuariosException.class));
        assertTrue(!RuntimeException.class.isAssignableFrom(ArquivoUsuariosException.class));
    }
}
