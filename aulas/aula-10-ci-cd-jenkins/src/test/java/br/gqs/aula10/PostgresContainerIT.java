package br.gqs.aula10;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Integração com um PostgreSQL real subido pelo TestContainers.
 * Sem Docker no ambiente (caso desta máquina) o teste é PULADO, não falha.
 */
@Testcontainers(disabledWithoutDocker = true)
class PostgresContainerIT {

    @Container
    private static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine");

    @Test
    void conectaEExecutaConsulta() throws SQLException {
        try (Connection conexao = DriverManager.getConnection(
                POSTGRES.getJdbcUrl(), POSTGRES.getUsername(), POSTGRES.getPassword());
             Statement comando = conexao.createStatement();
             ResultSet resultado = comando.executeQuery("SELECT 1")) {
            resultado.next();
            assertEquals(1, resultado.getInt(1));
        }
    }
}
