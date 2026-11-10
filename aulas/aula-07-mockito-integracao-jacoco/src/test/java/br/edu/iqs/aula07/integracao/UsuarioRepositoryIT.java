package br.edu.iqs.aula07.integracao;

import javax.sql.DataSource;

import org.postgresql.ds.PGSimpleDataSource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Atividade 2: teste de integracao com PostgreSQL REAL em container (Testcontainers).
 * <p>
 * Fluxo: o Testcontainers sobe o container Docker com PostgreSQL, o contrato de testes executa
 * contra ele e o container e destruido ao final.
 * <p>
 * {@code disabledWithoutDocker = true}: sem Docker (como na maquina onde esta aula foi
 * desenvolvida) os testes sao PULADOS (Skipped) em vez de falhar. Eles rodam de verdade no GitHub
 * Actions (runner ubuntu-latest tem Docker): ver {@code .github/workflows/aula-07-ci.yml}.
 */
@Testcontainers(disabledWithoutDocker = true)
class UsuarioRepositoryIT extends UsuarioJdbcRepositoryContrato {

    // Slide: postgres:15. Container estatico = um unico container para todos os testes da classe.
    @Container
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    @Override
    protected DataSource criarDataSource() {
        // "Configurar DataSource com a URL do container" (comentario do slide, aqui implementado)
        PGSimpleDataSource ds = new PGSimpleDataSource();
        ds.setUrl(postgres.getJdbcUrl());
        ds.setUser(postgres.getUsername());
        ds.setPassword(postgres.getPassword());
        return ds;
    }
}
