package com.example.projeto.domain.service;

/**
 * Lancada quando um produto solicitado nao existe (mapeada para HTTP 404).
 */
public class ProdutoNaoEncontradoException extends RuntimeException {

    public ProdutoNaoEncontradoException(Long id) {
        super("Produto nao encontrado: id=" + id);
    }
}
