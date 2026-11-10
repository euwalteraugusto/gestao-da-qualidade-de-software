package br.gqs.aula10.pedido;

import java.util.Optional;

/** Porta de persistência de pedidos (implementável em memória, JDBC etc.). */
public interface RepositorioPedidos {

    /**
     * Salva (ou substitui) um pedido.
     *
     * @param pedido pedido a salvar
     */
    void salvar(Pedido pedido);

    /**
     * Busca um pedido pelo identificador.
     *
     * @param id identificador do pedido
     * @return o pedido, ou vazio se não existir
     */
    Optional<Pedido> buscar(String id);
}
