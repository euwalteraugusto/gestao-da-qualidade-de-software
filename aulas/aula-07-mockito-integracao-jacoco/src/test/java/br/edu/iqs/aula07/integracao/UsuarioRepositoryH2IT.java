package br.edu.iqs.aula07.integracao;

import javax.sql.DataSource;

import org.h2.jdbcx.JdbcDataSource;

/**
 * Executa o contrato de integracao contra o H2 em memoria (modo PostgreSQL). Nao precisa de
 * Docker, entao roda localmente e no CI. NAO substitui o teste com PostgreSQL real
 * ({@link UsuarioRepositoryIT}); serve para validar a logica JDBC/transacional em qualquer maquina.
 */
class UsuarioRepositoryH2IT extends UsuarioJdbcRepositoryContrato {

    @Override
    protected DataSource criarDataSource() {
        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL("jdbc:h2:mem:aula07;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1");
        ds.setUser("sa");
        ds.setPassword("");
        return ds;
    }
}
