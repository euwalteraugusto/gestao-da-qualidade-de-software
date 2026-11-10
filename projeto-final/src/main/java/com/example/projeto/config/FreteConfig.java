package com.example.projeto.config;

import com.example.projeto.domain.pedido.frete.CalculadoraFrete;
import com.example.projeto.domain.pedido.frete.CalculadoraFreteComLog;
import com.example.projeto.domain.pedido.frete.FreteFixo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

/**
 * Monta a cadeia de frete: {@code FreteFixo} decorado por {@code CalculadoraFreteComLog}.
 * Trocar a regra de frete (ex.: por regiao) e trocar o componente interno aqui.
 */
@Configuration
public class FreteConfig {

    @Bean
    public CalculadoraFrete calculadoraFrete(
            @Value("${app.frete.valor-minimo-frete-gratis:100.00}") BigDecimal minimoFreteGratis,
            @Value("${app.frete.valor-fixo:15.00}") BigDecimal valorFixo) {
        return new CalculadoraFreteComLog(new FreteFixo(minimoFreteGratis, valorFixo));
    }
}
