package com.example.projeto.health;

import com.example.projeto.integration.CepService;
import com.example.projeto.integration.Endereco;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

/**
 * Indicador de saude customizado (aula 19): verifica se a API externa ViaCEP
 * responde, consultando um CEP de referencia. Aparece em {@code /actuator/health}
 * como o componente {@code apiExterna} (nome do bean sem o sufixo "HealthIndicator").
 * <p>
 * O slide deixava {@code checkApiExterna()} indefinido e usava comentarios com
 * {@code #} dentro do Java (nao compila).
 */
@Component
public class ApiExternaHealthIndicator implements HealthIndicator {

    private final CepService cepService;
    private final String cepReferencia;

    public ApiExternaHealthIndicator(CepService cepService,
                                     @Value("${app.health.cep-referencia:01001000}") String cepReferencia) {
        this.cepService = cepService;
        this.cepReferencia = cepReferencia;
    }

    @Override
    public Health health() {
        long inicio = System.nanoTime();
        try {
            Endereco endereco = cepService.buscarPorCep(cepReferencia);
            return Health.up()
                    .withDetail("api", "ViaCEP")
                    .withDetail("cepReferencia", endereco.cep())
                    .withDetail("tempoRespostaMs", (System.nanoTime() - inicio) / 1_000_000)
                    .build();
        } catch (RuntimeException e) {
            return Health.down()
                    .withDetail("api", "ViaCEP")
                    .withDetail("erro", "API externa indisponivel")
                    .withDetail("causa", e.getClass().getSimpleName())
                    .build();
        }
    }
}
