package br.gqs.aula12.api;

/** Lançada quando o usuário pedido não existe. */
public class UsuarioNaoEncontradoException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Cria a exceção.
     *
     * @param id identificador não encontrado
     */
    public UsuarioNaoEncontradoException(Long id) {
        super("Usuário " + id + " não encontrado");
    }
}
