package com.example.projeto.domain.repository;

import com.example.projeto.domain.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio Spring Data JPA de {@link Produto}.
 */
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
}
