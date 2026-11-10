package com.example.projeto.interfaces.rest;

import com.example.projeto.domain.model.Produto;
import com.example.projeto.domain.repository.ProdutoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.containsString;
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

    private static final String URL = "/produtos";

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ProdutoRepository repository;

    @Test
    void deveListarProdutosVazios() throws Exception {
        mockMvc.perform(get(URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void deveCriarProduto() throws Exception {
        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Notebook\",\"preco\":3500.00}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.nome", is("Notebook")));
    }

    @Test
    void deveBuscarAtualizarERemoverProduto() throws Exception {
        Produto p = repository.save(new Produto("Mouse", new BigDecimal("50.00")));

        mockMvc.perform(get(URL + "/{id}", p.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is("Mouse")));

        mockMvc.perform(put(URL + "/{id}", p.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Mouse Gamer\",\"preco\":80.00}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is("Mouse Gamer")));

        mockMvc.perform(delete(URL + "/{id}", p.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void deveBuscarProdutosPorNome() throws Exception {
        repository.save(new Produto("Smartphone Xiaomi"));
        repository.save(new Produto("Notebook Dell"));

        mockMvc.perform(get(URL + "/buscar?nome=xiaomi"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nome", containsString("Xiaomi")));
    }

    @Test
    void deveRetornarListaVaziaQuandoBuscaNaoEncontra() throws Exception {
        mockMvc.perform(get(URL + "/buscar?nome=inexistente"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void deveRetornar400QuandoParametroDeBuscaAusente() throws Exception {
        mockMvc.perform(get(URL + "/buscar"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail", containsString("nome")));
    }

    @Test
    void deveRetornar404ParaProdutoInexistente() throws Exception {
        mockMvc.perform(get(URL + "/{id}", 9999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title", is("Recurso nao encontrado")));
        mockMvc.perform(put(URL + "/{id}", 9999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"X\",\"preco\":1.00}"))
                .andExpect(status().isNotFound());
        mockMvc.perform(delete(URL + "/{id}", 9999)).andExpect(status().isNotFound());
    }

    /** US-002 (tarefa pos-aula 15): validacao de campos obrigatorios. */
    @ParameterizedTest(name = "[{index}] {0} -> campo {1}")
    @CsvSource(delimiter = '|', value = {
            "{\"preco\":10.00}                  | nome  | nome e obrigatorio",
            "{\"nome\":\"   \",\"preco\":10.00} | nome  | nome e obrigatorio",
            "{\"nome\":\"Cabo\"}                | preco | preco e obrigatorio",
            "{\"nome\":\"Cabo\",\"preco\":0}    | preco | preco deve ser maior que zero",
            "{\"nome\":\"Cabo\",\"preco\":-5}   | preco | preco deve ser maior que zero"
    })
    void deveRejeitarCamposObrigatoriosInvalidos(String corpo, String campo, String mensagem) throws Exception {
        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpo))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title", is("Erro de validacao")))
                .andExpect(jsonPath("$.campos." + campo, is(mensagem)));
    }
}
