package com.example.projeto.web;

import com.example.projeto.integration.CepNaoEncontradoException;
import com.example.projeto.integration.CepService;
import com.example.projeto.integration.Endereco;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.ResourceAccessException;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * Pagina de CEP e tratamento de erros da API externa ({@link WebExceptionHandler}).
 * O {@link CepService} e substituido por um mock: o teste nao depende da internet.
 */
@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "cliente", roles = "USER")
class CepWebControllerIT {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    CepService cepService;

    @Test
    void deveExibirFormularioSemConsultar() throws Exception {
        mockMvc.perform(get("/cep"))
                .andExpect(status().isOk())
                .andExpect(view().name("cep"))
                .andExpect(model().attributeDoesNotExist("endereco"));
    }

    @Test
    void deveExibirEnderecoEncontrado() throws Exception {
        when(cepService.buscarPorCep("01001-000")).thenReturn(
                new Endereco("01001-000", "Praca da Se", null, "Se", "Sao Paulo", "SP", null));

        mockMvc.perform(get("/cep").param("cep", "01001-000"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Praca da Se")))
                .andExpect(content().string(containsString("Sao Paulo/SP")));
    }

    @Test
    void deveExibirPaginaDeErroQuandoApiExternaFalha() throws Exception {
        when(cepService.buscarPorCep("01001000")).thenThrow(new ResourceAccessException("timeout"));

        mockMvc.perform(get("/cep").param("cep", "01001000"))
                .andExpect(status().isBadGateway())
                .andExpect(view().name("error"))
                .andExpect(content().string(containsString("Falha ao consultar API externa")));
    }

    @Test
    void deveExibirErro404ParaCepInexistente() throws Exception {
        when(cepService.buscarPorCep("99999999")).thenThrow(new CepNaoEncontradoException("99999999"));

        mockMvc.perform(get("/cep").param("cep", "99999999"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("CEP nao encontrado")));
    }

    @Test
    void deveExibirErro400ParaCepInvalido() throws Exception {
        when(cepService.buscarPorCep("abc")).thenThrow(new IllegalArgumentException("CEP invalido"));

        mockMvc.perform(get("/cep").param("cep", "abc"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("CEP invalido")));
    }
}
