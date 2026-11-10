package com.example.projeto.domain.repository;

import com.example.projeto.domain.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repositorio Spring Data JPA de {@link Produto}.
 */
public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    /**
     * US-001: busca produtos cujo nome contem o texto informado (sem diferenciar maiusculas).
     * Query method: o Spring Data gera o JPQL a partir do nome do metodo.
     */
    List<Produto> findByNomeContainingIgnoreCase(String nome);
}
