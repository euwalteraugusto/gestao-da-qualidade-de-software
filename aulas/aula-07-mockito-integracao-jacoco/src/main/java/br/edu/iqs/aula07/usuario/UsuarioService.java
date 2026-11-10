package br.edu.iqs.aula07.usuario;

import java.util.List;
import java.util.Objects;

/** Servico de usuarios; depende de {@link UsuarioRepository} (mockado nos testes unitarios). */
public class UsuarioService {

    private final UsuarioRepository repository;

    public UsuarioService(UsuarioRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    /**
     * Busca um usuario pelo id.
     *
     * @throws UsuarioNaoEncontradoException se o repositorio devolver {@code null}
     */
    public Usuario buscarUsuario(Long id) {
        Usuario usuario = repository.buscarPorId(id);
        if (usuario == null) {
            throw new UsuarioNaoEncontradoException(id);
        }
        return usuario;
    }

    /**
     * Cria e salva um usuario.
     *
     * @throws IllegalArgumentException se o nome for nulo ou em branco (nada e salvo)
     */
    public Usuario salvarUsuario(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome é obrigatório");
        }
        Usuario usuario = new Usuario(nome.strip());
        repository.salvar(usuario);
        return usuario;
    }

    public List<Usuario> listarUsuarios() {
        return repository.buscarTodos();
    }
}
