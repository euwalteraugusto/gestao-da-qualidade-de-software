package com.example.projeto.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.HttpServerErrorException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * Testa a integracao com o ViaCEP sem acessar a internet: o
 * {@link MockRestServiceServer} simula as respostas HTTP.
 */
@RestClientTest(CepService.class)
@TestPropertySource(properties = "app.viacep.url=https://viacep.test/ws")
class CepServiceTest {

    @Autowired
    CepService cepService;

    @Autowired
    MockRestServiceServer server;

    @Test
    void deveBuscarEnderecoPorCepComMascara() {
        server.expect(requestTo("https://viacep.test/ws/01001000/json/"))
                .andRespond(withSuccess("""
                        {"cep":"01001-000","logradouro":"Praca da Se","bairro":"Se",
                         "localidade":"Sao Paulo","uf":"SP","ibge":"3550308"}
                        """, MediaType.APPLICATION_JSON));

        Endereco endereco = cepService.buscarPorCep("01001-000");

        assertThat(endereco.localidade()).isEqualTo("Sao Paulo");
        assertThat(endereco.uf()).isEqualTo("SP");
        server.verify();
    }

    @Test
    void deveLancarCepNaoEncontradoQuandoViaCepRetornaErro() {
        server.expect(requestTo("https://viacep.test/ws/99999999/json/"))
                .andRespond(withSuccess("{\"erro\": true}", MediaType.APPLICATION_JSON));

        assertThatThrownBy(() -> cepService.buscarPorCep("99999999"))
                .isInstanceOf(CepNaoEncontradoException.class)
                .hasMessageContaining("99999999");
    }

    @Test
    void devePropagarFalhaDaApiExterna() {
        server.expect(requestTo("https://viacep.test/ws/01001000/json/"))
                .andRespond(withStatus(HttpStatus.SERVICE_UNAVAILABLE));

        assertThatThrownBy(() -> cepService.buscarPorCep("01001000"))
                .isInstanceOf(HttpServerErrorException.class);
    }

    @Test
    void deveRejeitarCepInvalidoSemChamarApi() {
        assertThatThrownBy(() -> cepService.buscarPorCep("../admin"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> cepService.buscarPorCep(null))
                .isInstanceOf(IllegalArgumentException.class);
        server.verify();
    }
}
