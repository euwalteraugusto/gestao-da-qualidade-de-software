package com.example.projeto.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Entidade JPA Produto (modelo de dominio do MVP).
 * <p>
 * Usa {@code @Getter/@Setter} em vez de {@code @Data}: em entidades JPA o
 * equals/hashCode gerado pelo {@code @Data} sobre todos os campos (inclusive o id
 * gerado) causa problemas em colecoes e proxies do Hibernate.
 */
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(precision = 10, scale = 2)
    private BigDecimal preco;

    public Produto(String nome) {
        this.nome = nome;
    }

    public Produto(String nome, BigDecimal preco) {
        this.nome = nome;
        this.preco = preco;
    }

    /**
     * Preco com 2 casas decimais (arredondamento HALF_UP).
     * <p>
     * Correcao apontada pela analise estatica (aula 15): a versao original chamava
     * {@code preco.setScale(...)} sem verificar nulo (possivel NullPointerException).
     * Com preco nulo retorna {@code 0.00} (o slide retornava {@code BigDecimal.ZERO},
     * com escala 0, inconsistente com o caso normal).
     */
    @JsonIgnore
    public BigDecimal getPrecoFormatado() {
        return preco != null
                ? preco.setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    }
}
