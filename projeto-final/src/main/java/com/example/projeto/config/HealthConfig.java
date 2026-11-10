package com.example.projeto.config;

import org.springframework.boot.actuate.jdbc.DataSourceHealthIndicator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * Health check do banco com query explicita (exemplo do slide).
 * <p>
 * O nome do bean e {@code dbHealthIndicator} de proposito: substitui o indicador
 * auto-configurado pelo Spring Boot (que e condicional a ausencia desse nome) em vez
 * de criar um segundo componente duplicado. Aparece como {@code db} no health.
 */
@Configuration
public class HealthConfig {

    @Bean
    public DataSourceHealthIndicator dbHealthIndicator(DataSource dataSource) {
        return new DataSourceHealthIndicator(dataSource, "SELECT 1");
    }
}
