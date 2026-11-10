package br.edu.iqs.aula08.carrinho;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * Carrinho de compras simples. Serve de exemplo para a tarefa "escrever uma feature (com pelo
 * menos 2 cenarios) usando Gherkin para o projeto final"; adapte o dominio ao seu projeto.
 */
public class Carrinho {

    /** Unico cupom conhecido: 10% de desconto. */
    public static final String CUPOM_DESCONTO_10 = "DESCONTO10";

    private record Item(String nome, BigDecimal precoUnitario, int quantidade) {
    }

    private final List<Item> itens = new ArrayList<>();
    private BigDecimal percentualDesconto = BigDecimal.ZERO;

    public void adicionar(String nome, BigDecimal precoUnitario, int quantidade) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero");
        }
        if (precoUnitario == null || precoUnitario.signum() < 0) {
            throw new IllegalArgumentException("Preço não pode ser negativo");
        }
        itens.add(new Item(nome, precoUnitario, quantidade));
    }

    public void aplicarCupom(String codigo) {
        if (CUPOM_DESCONTO_10.equals(codigo)) {
            percentualDesconto = new BigDecimal("0.10");
        } else {
            throw new CupomInvalidoException(codigo);
        }
    }

    /** Total de unidades no carrinho. */
    public int getQuantidadeItens() {
        return itens.stream().mapToInt(Item::quantidade).sum();
    }

    /** Total a pagar, ja com desconto, com 2 casas decimais. */
    public BigDecimal getTotal() {
        BigDecimal subtotal = itens.stream()
                .map(i -> i.precoUnitario().multiply(BigDecimal.valueOf(i.quantidade())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return subtotal.multiply(BigDecimal.ONE.subtract(percentualDesconto)).setScale(2, RoundingMode.HALF_UP);
    }
}
