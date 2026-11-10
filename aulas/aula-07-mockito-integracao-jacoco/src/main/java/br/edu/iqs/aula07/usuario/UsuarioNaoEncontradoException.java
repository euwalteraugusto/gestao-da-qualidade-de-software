package br.edu.iqs.aula07.usuario;

/** Nenhum usuario com o id informado (exemplo citado na Aula 5). */
public class UsuarioNaoEncontradoException extends RuntimeException {

    public UsuarioNaoEncontradoException(Long id) {
        super("Usuário não encontrado. ID: " + id);
    }
}
