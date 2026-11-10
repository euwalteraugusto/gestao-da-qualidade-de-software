package com.example.projeto.web;

import com.example.projeto.domain.model.Produto;
import com.example.projeto.domain.repository.ProdutoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * Interface web Thymeleaf: listagem, formulario, validacao e PRG.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@WithMockUser(username = "admin", roles = {"ADMIN", "USER"})
class ProdutoWebControllerIT {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ProdutoRepository repository;

    @Test
    void deveRenderizarListaComPrecoFormatado() throws Exception {
        repository.save(new Produto("Teclado Mecanico", new BigDecimal("349.9")));

        mockMvc.perform(get("/produtos"))
                .andExpect(status().isOk())
                .andExpect(view().name("produtos/lista"))
                .andExpect(model().attribute("produtos", hasSize(1)))
                .andExpect(content().string(containsString("Teclado Mecanico")))
                .andExpect(content().string(containsString("R$ 349,90")))
                .andExpect(content().string(containsString("Novo produto")))
                .andExpect(content().string(containsString("Sair")));
    }

    @Test
    void deveExibirFormularioVazio() throws Exception {
        mockMvc.perform(get("/produtos/novo"))
                .andExpect(status().isOk())
                .andExpect(view().name("produtos/form"))
                .andExpect(model().attributeExists("form"));
    }

    @Test
    void deveCadastrarERedirecionar() throws Exception {
        mockMvc.perform(post("/produtos/cadastrar").with(csrf()).param("nome", "Webcam").param("preco", "199.90"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/produtos"));

        assertThat(repository.findByNomeContainingIgnoreCase("webcam")).hasSize(1);
    }

    @Test
    void deveReexibirFormularioComErrosDeValidacao() throws Exception {
        mockMvc.perform(post("/produtos/cadastrar").with(csrf()).param("nome", "").param("preco", "-1"))
                .andExpect(status().isOk())
                .andExpect(view().name("produtos/form"))
                .andExpect(model().attributeHasFieldErrors("form", "nome", "preco"))
                .andExpect(content().string(containsString("Informe o nome do produto")));
    }

    @Test
    @WithMockUser(username = "cliente", roles = "USER")
    void clienteNaoVeBotaoDeCadastro() throws Exception {
        mockMvc.perform(get("/produtos"))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("Novo produto"))))
                .andExpect(content().string(containsString("cliente")));
    }

    @Test
    void raizRedirecionaParaProdutos() throws Exception {        mockMvc.perform(get("/"))
                .andExpect(redirectedUrl("/produtos"));
    }
}
