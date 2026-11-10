package br.edu.iqs.aula07.produto;

import java.util.Optional;

/** Porta de persistencia de produtos (mockada nos testes do {@link ProdutoService}). */
public interface ProdutoRepository {

    Optional<Produto> buscarPorId(long id);
}
