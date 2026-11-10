package br.edu.iqs.aula07.usuario;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Atividade 1: Mockito. Usa {@code @ExtendWith(MockitoExtension.class)} no lugar de
 * {@code MockitoAnnotations.openMocks(this)} do slide: a extensao inicializa e FECHA os mocks e
 * ativa o modo "strict stubs" (falha se um stub configurado nao for usado).
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UsuarioService (com UsuarioRepository mockado)")
class UsuarioServiceTest {

    @Mock
    UsuarioRepository repository;

    @InjectMocks
    UsuarioService service;

    @Test
    @DisplayName("Retorna o usuário quando ele existe (exemplo do slide)")
    void deveRetornarUsuarioQuandoExistir() {
        when(repository.buscarPorId(1L)).thenReturn(new Usuario("João"));

        Usuario resultado = service.buscarUsuario(1L);

        assertEquals("João", resultado.getNome());
        verify(repository, times(1)).buscarPorId(1L);
        verifyNoMoreInteractions(repository);
    }

    @Test
    @DisplayName("Exercício: buscarPorId retorna null -> UsuarioNaoEncontradoException")
    void deveLancarExcecaoQuandoRepositorioRetornarNull() {
        when(repository.buscarPorId(99L)).thenReturn(null);

        UsuarioNaoEncontradoException e = assertThrows(UsuarioNaoEncontradoException.class,
                () -> service.buscarUsuario(99L));

        assertTrue(e.getMessage().contains("99"), e.getMessage());
        verify(repository).buscarPorId(99L);
    }

    @Test
    @DisplayName("Exercício: salvarUsuario cria o usuário e chama repository.salvar uma vez")
    void deveSalvarUsuario() {
        // Simula o banco preenchendo o id gerado (salvar() e void, entao usamos doAnswer)
        doAnswer(invocacao -> {
            Usuario u = invocacao.getArgument(0);
            u.setId(10L);
            return null;
        }).when(repository).salvar(any(Usuario.class));

        Usuario salvo = service.salvarUsuario("  Maria  ");

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(repository, times(1)).salvar(captor.capture());
        assertEquals("Maria", captor.getValue().getNome(), "o nome deve ser aparado (strip)");
        assertSame(salvo, captor.getValue());
        assertEquals(10L, salvo.getId());
    }

    @ParameterizedTest(name = "nome [{0}] é rejeitado e nada é salvo")
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t"})
    @DisplayName("salvarUsuario rejeita nome nulo/vazio/em branco sem tocar no repositório")
    void naoDeveSalvarNomeInvalido(String nome) {
        assertThrows(IllegalArgumentException.class, () -> service.salvarUsuario(nome));
        verify(repository, never()).salvar(any());
    }

    @Test
    @DisplayName("Falha do repositório (thenThrow/doThrow) propaga para o chamador")
    void devePropagarFalhaDoRepositorio() {
        doThrow(new PersistenciaException("banco fora do ar", new RuntimeException("causa")))
                .when(repository).salvar(any(Usuario.class));

        PersistenciaException e = assertThrows(PersistenciaException.class, () -> service.salvarUsuario("Ana"));
        assertNotNull(e.getCause());
    }

    @Test
    @DisplayName("listarUsuarios delega para buscarTodos")
    void deveListarUsuarios() {
        List<Usuario> todos = List.of(new Usuario("A"), new Usuario("B"));
        when(repository.buscarTodos()).thenReturn(todos);

        assertSame(todos, service.listarUsuarios());
        verify(repository).buscarTodos();
    }

    @Test
    @DisplayName("Repositório nulo no construtor é rejeitado")
    void construtorRejeitaRepositorioNulo() {
        assertThrows(NullPointerException.class, () -> new UsuarioService(null));
    }
}
