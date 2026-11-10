package com.example.projeto.interfaces.rest;

import com.example.projeto.domain.model.Produto;
import com.example.projeto.domain.repository.ProdutoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Teste de integracao do CRUD de produtos (contexto Spring completo + H2).
 * {@code @Transactional} faz rollback ao fim de cada teste (isolamento - FIRST).
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ProdutoControllerIT {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ProdutoRepository repository;

    private Produto salvar(String nome, String preco) {
        Produto p = new Produto();
        p.setNome(nome);
        p.setPreco(new BigDecimal(preco));
        return repository.save(p);
    }

    @Test
    void deveListarProdutosVazios() throws Exception {
        mockMvc.perform(get("/produtos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void deveCriarProduto() throws Exception {
        mockMvc.perform(post("/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Notebook\",\"preco\":3500.00}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.nome", is("Notebook")));
    }

    @Test
    void deveBuscarAtualizarERemoverProduto() throws Exception {
        Produto p = salvar("Mouse", "50.00");

        mockMvc.perform(get("/produtos/{id}", p.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is("Mouse")));

        mockMvc.perform(put("/produtos/{id}", p.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Mouse Gamer\",\"preco\":80.00}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is("Mouse Gamer")));

        mockMvc.perform(delete("/produtos/{id}", p.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void deveRetornar404ParaProdutoInexistente() throws Exception {
        mockMvc.perform(get("/produtos/{id}", 9999)).andExpect(status().isNotFound());
        mockMvc.perform(put("/produtos/{id}", 9999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"X\",\"preco\":1.00}"))
                .andExpect(status().isNotFound());
        mockMvc.perform(delete("/produtos/{id}", 9999)).andExpect(status().isNotFound());
    }
}
