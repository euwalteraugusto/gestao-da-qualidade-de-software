package com.example.projeto.domain.repository;

import com.example.projeto.domain.model.Produto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testa apenas a camada JPA (dica da aula 15: {@code @DataJpaTest}).
 */
@DataJpaTest
class ProdutoRepositoryTest {

    @Autowired
    ProdutoRepository repository;

    @Test
    void deveBuscarPorNomeIgnorandoMaiusculas() {
        repository.save(new Produto("Smartphone Xiaomi", new BigDecimal("1500.00")));
        repository.save(new Produto("Notebook Dell", new BigDecimal("4000.00")));

        List<Produto> encontrados = repository.findByNomeContainingIgnoreCase("XIAOMI");

        assertThat(encontrados).extracting(Produto::getNome).containsExactly("Smartphone Xiaomi");
    }
}
