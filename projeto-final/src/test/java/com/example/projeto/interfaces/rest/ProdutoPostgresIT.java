package com.example.projeto.interfaces.rest;

import com.example.projeto.domain.model.Produto;
import com.example.projeto.domain.repository.ProdutoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Teste de integracao contra um PostgreSQL REAL em container (Testcontainers).
 * <p>
 * {@code disabledWithoutDocker = true}: sem Docker (ex.: maquina de desenvolvimento
 * sem Docker) a classe e marcada como "skipped" em vez de falhar. No GitHub Actions
 * (ubuntu-latest tem Docker) o teste roda de verdade.
 */
@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest
@AutoConfigureMockMvc
class ProdutoPostgresIT {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    @DynamicPropertySource
    static void configure(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Autowired
    ProdutoRepository repository;

    @Autowired
    MockMvc mockMvc;

    @Test
    void devePersistirProdutoNoBancoReal() throws Exception {
        Produto salvo = repository.save(new Produto("Smartphone Xiaomi", new BigDecimal("1999.90")));

        assertThat(salvo.getId()).isNotNull();
        assertThat(repository.findById(salvo.getId())).get()
                .extracting(Produto::getPreco).isEqualTo(new BigDecimal("1999.90"));

        mockMvc.perform(get("/api/produtos/buscar?nome=xiaomi"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome", containsString("Xiaomi")));
    }
}
