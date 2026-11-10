package br.edu.iqs.aula07.integracao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import br.edu.iqs.aula07.usuario.PersistenciaException;
import br.edu.iqs.aula07.usuario.Usuario;
import br.edu.iqs.aula07.usuario.UsuarioJdbcRepository;

/**
 * Contrato de testes do {@link UsuarioJdbcRepository} contra um banco REAL (sem mocks).
 * <p>
 * A classe e abstrata: cada subclasse fornece o {@link DataSource}:
 * <ul>
 *   <li>{@code UsuarioRepositoryIT}: PostgreSQL 15 em container (Testcontainers) - requer Docker;</li>
 *   <li>{@code UsuarioRepositoryH2IT}: H2 em memoria (modo PostgreSQL) - roda em qualquer maquina.</li>
 * </ul>
 * Assim os MESMOS cenarios (incluindo buscarTodos e rollback) sao validados nos dois bancos.
 * O nome nao termina em Test/IT de proposito, para nao ser executada diretamente.
 */
abstract class UsuarioJdbcRepositoryContrato {

    protected UsuarioJdbcRepository repository;
    private DataSource dataSource;

    /** Fornece o DataSource do banco sob teste (ja apontando para o container/banco em execucao). */
    protected abstract DataSource criarDataSource();

    @BeforeEach
    void prepararBanco() throws SQLException {
        dataSource = criarDataSource();
        repository = new UsuarioJdbcRepository(dataSource);
        repository.criarTabelaSeNecessario();
        try (Connection c = dataSource.getConnection(); Statement st = c.createStatement()) {
            st.execute("DELETE FROM usuarios"); // isolamento entre os testes
        }
    }

    @Test
    @DisplayName("deveSalvarUsuarioNoBanco (slide): salva, gera id e busca de volta")
    void deveSalvarUsuarioNoBanco() {
        Usuario usuario = new Usuario("Maria");
        repository.salvar(usuario);

        assertNotNull(usuario.getId(), "o banco deve gerar o id");
        Usuario salvado = repository.buscarPorId(usuario.getId());
        assertEquals("Maria", salvado.getNome());
        assertEquals(usuario.getId(), salvado.getId());
    }

    @Test
    @DisplayName("buscarPorId de id inexistente devolve null")
    void buscarPorIdInexistente() {
        assertNull(repository.buscarPorId(123456L));
    }

    @Test
    @DisplayName("Exercício buscarTodos: devolve todos, ordenados por id")
    void deveBuscarTodos() {
        assertTrue(repository.buscarTodos().isEmpty(), "tabela deveria começar vazia");

        repository.salvar(new Usuario("Ana"));
        repository.salvar(new Usuario("Bruno"));
        repository.salvar(new Usuario("Carla"));

        List<Usuario> todos = repository.buscarTodos();
        assertEquals(List.of("Ana", "Bruno", "Carla"), todos.stream().map(Usuario::getNome).toList());
        assertTrue(todos.get(0).getId() < todos.get(1).getId());
        assertTrue(todos.get(1).getId() < todos.get(2).getId());
    }

    @Test
    @DisplayName("salvarTodos grava todos quando não há erro (commit)")
    void salvarTodosComCommit() {
        repository.salvarTodos(List.of(new Usuario("Dora"), new Usuario("Eli")));

        assertEquals(2, repository.buscarTodos().size());
    }

    @Test
    @DisplayName("Exercício rollback: se um item falha, NENHUM do lote fica gravado")
    void salvarTodosComRollback() {
        Usuario primeiro = new Usuario("Fabio");
        Usuario duplicado = new Usuario("Fabio"); // viola a restricao UNIQUE(nome)

        PersistenciaException e = assertThrows(PersistenciaException.class,
                () -> repository.salvarTodos(List.of(primeiro, new Usuario("Gina"), duplicado)));

        assertNotNull(e.getCause(), "a SQLException original deve ser preservada");
        assertTrue(repository.buscarTodos().isEmpty(), "rollback: nada pode ter sido gravado");
        assertNull(primeiro.getId(), "id em memoria deve ser desfeito junto com o rollback");
    }

    @Test
    @DisplayName("Nome duplicado em salvar() lança PersistenciaException e não altera o banco")
    void nomeDuplicado() {
        repository.salvar(new Usuario("Hugo"));

        assertThrows(PersistenciaException.class, () -> repository.salvar(new Usuario("Hugo")));

        assertEquals(1, repository.buscarTodos().size());
    }

    @Test
    @DisplayName("Nome nulo viola NOT NULL e lança PersistenciaException")
    void nomeNulo() {
        assertThrows(PersistenciaException.class, () -> repository.salvar(new Usuario(null)));
    }
}
