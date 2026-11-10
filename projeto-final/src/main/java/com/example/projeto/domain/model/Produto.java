package com.example.projeto.domain.model;

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
}
