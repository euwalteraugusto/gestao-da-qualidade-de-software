package br.edu.iqs.aula07.produto;

/** Nenhum produto com o id informado. */
public class ProdutoNaoEncontradoException extends RuntimeException {

    public ProdutoNaoEncontradoException(long id) {
        super("Produto não encontrado. ID: " + id);
    }
}
