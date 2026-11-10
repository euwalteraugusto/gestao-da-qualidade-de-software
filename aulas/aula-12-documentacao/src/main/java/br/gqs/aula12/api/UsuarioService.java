package br.gqs.aula12.api;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Service;

/** Regras de usuários com armazenamento em memória (suficiente para a demonstração). */
@Service
public class UsuarioService {

    private final Map<Long, Usuario> usuarios = new ConcurrentHashMap<>();
    private final AtomicLong sequencia = new AtomicLong();

    /**
     * Busca um usuário.
     *
     * @param id identificador do usuário
     * @return o usuário encontrado
     * @throws UsuarioNaoEncontradoException se não existir
     */
    public Usuario buscar(Long id) {
        Usuario usuario = usuarios.get(id);
        if (usuario == null) {
            throw new UsuarioNaoEncontradoException(id);
        }
        return usuario;
    }

    /**
     * Cria um usuário com novo identificador.
     *
     * @param pedido dados do novo usuário
     * @return o usuário criado
     */
    public Usuario criar(NovoUsuarioRequest pedido) {
        Usuario usuario = new Usuario(sequencia.incrementAndGet(), pedido.nome(), pedido.email());
        usuarios.put(usuario.id(), usuario);
        return usuario;
    }
}
