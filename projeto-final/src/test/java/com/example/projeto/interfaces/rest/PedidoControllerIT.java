package com.example.projeto.interfaces.rest;

import com.example.projeto.domain.model.Produto;
import com.example.projeto.domain.repository.ProdutoRepository;
import com.example.projeto.support.MockMvcComJwt;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * US-003: total do pedido com desconto por tipo de cliente e frete (fim a fim).
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PedidoControllerIT {

    private static final String URL = "/api/pedidos/total";

    @Autowired
    WebApplicationContext contexto;

    /** Requisicoes autenticadas com um access token JWT real do usuario admin (aula 18). */
    MockMvc mockMvc;

    @BeforeEach
    void autenticarComoAdmin() {
        mockMvc = MockMvcComJwt.criar(contexto, "admin");
    }

    @Autowired
    ProdutoRepository repository;

    @Test
    void deveCalcularTotalParaClientePremiumComFrete() throws Exception {
        Produto fone = repository.save(new Produto("Fone", new BigDecimal("40.00")));
        Produto cabo = repository.save(new Produto("Cabo", new BigDecimal("15.00")));

        String corpo = """
                {"tipoCliente":"PREMIUM","itens":[
                  {"produtoId":%d,"quantidade":2},
                  {"produtoId":%d,"quantidade":2}]}
                """.formatted(fone.getId(), cabo.getId());

        // subtotal 110.00 -> 20% off = 88.00 -> abaixo de 100 -> frete 15.00 -> total 103.00
        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(corpo))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.subtotal", is(110.00)))
                .andExpect(jsonPath("$.desconto", is(22.00)))
                .andExpect(jsonPath("$.frete", is(15.00)))
                .andExpect(jsonPath("$.total", is(103.00)));
    }

    @Test
    void deveRetornar404QuandoProdutoDoPedidoNaoExiste() throws Exception {
        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tipoCliente\":\"VIP\",\"itens\":[{\"produtoId\":987654,\"quantidade\":1}]}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveRetornar400ParaTipoClienteDesconhecido() throws Exception {
        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tipoCliente\":\"OURO\",\"itens\":[{\"produtoId\":1,\"quantidade\":1}]}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title", is("Requisicao invalida")));
    }

    @Test
    void deveValidarRequisicao() throws Exception {
        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"itens\":[]}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos.tipoCliente", is("tipoCliente e obrigatorio")))
                .andExpect(jsonPath("$.campos.itens", is("itens e obrigatorio")));
    }
}
