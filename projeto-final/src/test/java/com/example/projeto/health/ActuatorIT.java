package com.example.projeto.health;

import com.example.projeto.integration.CepService;
import com.example.projeto.integration.Endereco;
import com.example.projeto.support.MockMvcComJwt;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Endpoints do Actuator (aula 19): health com indicador customizado, info e metrics protegido.
 * A API externa e mockada para o teste nao depender da internet.
 */
@SpringBootTest
@AutoConfigureMockMvc
class ActuatorIT {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    WebApplicationContext contexto;

    @MockBean
    CepService cepService;

    @BeforeEach
    void apiExternaNoAr() {
        when(cepService.buscarPorCep("01001000"))
                .thenReturn(new Endereco("01001-000", "Praca da Se", null, "Se", "Sao Paulo", "SP", null));
    }

    @Test
    void healthPublicoComComponentesApiExternaEDb() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("UP")))
                .andExpect(jsonPath("$.components.apiExterna.status", is("UP")))
                .andExpect(jsonPath("$.components.db.status", is("UP")))
                .andExpect(jsonPath("$.components.db.details.validationQuery", is("SELECT 1")));
    }

    @Test
    void healthDownQuandoApiExternaCai() throws Exception {
        when(cepService.buscarPorCep("01001000")).thenThrow(new ResourceAccessException("timeout"));

        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.components.apiExterna.status", is("DOWN")))
                .andExpect(jsonPath("$.components.apiExterna.details.erro", is("API externa indisponivel")));
    }

    @Test
    void infoPublicoComVersao() throws Exception {
        mockMvc.perform(get("/actuator/info"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.app.name", is("projeto-final")))
                .andExpect(jsonPath("$.app.version", notNullValue()));
    }

    @Test
    void metricsExigeAdmin() throws Exception {
        mockMvc.perform(get("/actuator/metrics")).andExpect(status().isUnauthorized());
        MockMvcComJwt.criar(contexto, "cliente").perform(get("/actuator/metrics"))
                .andExpect(status().isForbidden());
        MockMvcComJwt.criar(contexto, "admin").perform(get("/actuator/metrics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.names", notNullValue()));
    }
}
