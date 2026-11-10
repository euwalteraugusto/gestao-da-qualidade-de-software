package br.edu.iqs.aula07.usuario;

import java.util.List;

/**
 * Porta de persistencia de usuarios. Nos testes unitarios e substituida por um mock do Mockito;
 * nos testes de integracao e exercitada pela implementacao JDBC contra um banco real.
 */
public interface UsuarioRepository {

    /** Persiste o usuario e preenche o seu {@code id} gerado. */
    void salvar(Usuario usuario);

    /**
     * Persiste todos os usuarios em UMA transacao: ou todos sao gravados, ou nenhum.
     */
    void salvarTodos(List<Usuario> usuarios);

    /** @return o usuario, ou {@code null} se nao existir */
    Usuario buscarPorId(Long id);

    /** @return todos os usuarios, ordenados por id */
    List<Usuario> buscarTodos();
}
