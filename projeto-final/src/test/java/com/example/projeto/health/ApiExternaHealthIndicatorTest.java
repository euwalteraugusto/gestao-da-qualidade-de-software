package com.example.projeto.health;

import com.example.projeto.integration.CepService;
import com.example.projeto.integration.Endereco;
import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.web.client.ResourceAccessException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ApiExternaHealthIndicatorTest {

    private final CepService cepService = mock(CepService.class);
    private final ApiExternaHealthIndicator indicador = new ApiExternaHealthIndicator(cepService, "01001000");

    @Test
    void upQuandoApiExternaResponde() {
        when(cepService.buscarPorCep("01001000"))
                .thenReturn(new Endereco("01001-000", "Praca da Se", null, "Se", "Sao Paulo", "SP", null));

        Health health = indicador.health();

        assertThat(health.getStatus()).isEqualTo(Status.UP);
        assertThat(health.getDetails()).containsEntry("api", "ViaCEP").containsKey("tempoRespostaMs");
    }

    @Test
    void downComDetalheQuandoApiExternaFalha() {
        when(cepService.buscarPorCep("01001000")).thenThrow(new ResourceAccessException("timeout"));

        Health health = indicador.health();

        assertThat(health.getStatus()).isEqualTo(Status.DOWN);
        assertThat(health.getDetails())
                .containsEntry("erro", "API externa indisponivel")
                .containsEntry("causa", "ResourceAccessException");
    }
}
