package br.edu.iqs.aula07.produto;

/** Produto do catalogo (imutavel). Produtos inativos nao podem ter estoque reservado. */
public record Produto(long id, String nome, boolean ativo) {
}
