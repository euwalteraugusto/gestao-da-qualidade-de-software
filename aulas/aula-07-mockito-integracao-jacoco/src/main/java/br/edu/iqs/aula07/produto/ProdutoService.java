package br.edu.iqs.aula07.produto;

import java.util.Objects;

/**
 * Desafio integrado da Aula 7: servico que depende de {@link ProdutoRepository} (banco) e de
 * {@link EstoqueClient} (HTTP), ambos mockados nos testes.
 */
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final EstoqueClient estoqueClient;

    public ProdutoService(ProdutoRepository produtoRepository, EstoqueClient estoqueClient) {
        this.produtoRepository = Objects.requireNonNull(produtoRepository, "produtoRepository");
        this.estoqueClient = Objects.requireNonNull(estoqueClient, "estoqueClient");
    }

    /**
     * Reserva estoque de um produto: valida a quantidade, confere que o produto existe e esta
     * ativo, e chama o servico externo de estoque.
     *
     * @return quantidade restante em estoque
     * @throws IllegalArgumentException     se a quantidade for menor ou igual a zero
     * @throws ProdutoNaoEncontradoException se o produto nao existir
     * @throws IllegalStateException        se o produto estiver inativo
     * @throws EstoqueInsuficienteException se o servico de estoque nao tiver unidades suficientes
     */
    public int reservarEstoque(long produtoId, int quantidade) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("A quantidade deve ser maior que zero");
        }
        Produto produto = produtoRepository.buscarPorId(produtoId)
                .orElseThrow(() -> new ProdutoNaoEncontradoException(produtoId));
        if (!produto.ativo()) {
            throw new IllegalStateException("Produto inativo: " + produto.nome());
        }
        return estoqueClient.reservar(produtoId, quantidade);
    }
}
