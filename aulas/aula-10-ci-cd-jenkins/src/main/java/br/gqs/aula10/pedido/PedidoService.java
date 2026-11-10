package br.gqs.aula10.pedido;

import java.math.BigDecimal;
import java.util.List;

/** Caso de uso: fechar um pedido aplicando o desconto e persistindo o resultado. */
public class PedidoService {

    private final CalculadoraDesconto calculadora;
    private final RepositorioPedidos repositorio;

    /**
     * Cria o serviço.
     *
     * @param calculadora regra de desconto
     * @param repositorio destino da persistência
     */
    public PedidoService(CalculadoraDesconto calculadora, RepositorioPedidos repositorio) {
        this.calculadora = calculadora;
        this.repositorio = repositorio;
    }

    /**
     * Fecha o pedido: soma os itens, aplica o desconto e salva.
     *
     * @param id identificador do pedido
     * @param itens itens comprados (ao menos um)
     * @return pedido salvo, com o total já descontado
     * @throws IllegalArgumentException se o id for vazio ou não houver itens
     */
    public Pedido fechar(String id, List<ItemPedido> itens) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Id do pedido é obrigatório");
        }
        if (itens == null || itens.isEmpty()) {
            throw new IllegalArgumentException("O pedido precisa ter ao menos um item");
        }
        BigDecimal subtotal = itens.stream()
                .map(ItemPedido::subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal total = subtotal.subtract(calculadora.calcular(subtotal));
        Pedido pedido = new Pedido(id, itens, total);
        repositorio.salvar(pedido);
        return pedido;
    }
}
