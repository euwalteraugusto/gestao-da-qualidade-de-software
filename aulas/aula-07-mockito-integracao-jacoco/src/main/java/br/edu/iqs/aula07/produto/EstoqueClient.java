package br.edu.iqs.aula07.produto;

/**
 * Cliente do servico externo de estoque (chamada HTTP). Nos testes do {@link ProdutoService} e
 * mockado; a implementacao real e {@link EstoqueHttpClient}.
 */
public interface EstoqueClient {

    /**
     * Reserva unidades de um produto.
     *
     * @return quantidade que sobrou em estoque apos a reserva
     * @throws EstoqueInsuficienteException se nao houver unidades suficientes
     * @throws EstoqueIndisponivelException se o servico de estoque falhar/nao responder
     */
    int reservar(long produtoId, int quantidade);
}
