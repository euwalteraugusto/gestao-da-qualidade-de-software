package br.edu.iqs.aula07.integracao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import redis.clients.jedis.JedisPooled;

/**
 * Tarefa do slide: TestContainers com Redis. Sobe um Redis real em container e exercita
 * SET/GET/TTL/INCR com o cliente Jedis.
 * <p>
 * Pulado automaticamente sem Docker ({@code disabledWithoutDocker = true}); roda no GitHub Actions.
 */
@Testcontainers(disabledWithoutDocker = true)
class RedisIT {

    private static final int PORTA_REDIS = 6379;

    @Container
    static final GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:7-alpine"))
            .withExposedPorts(PORTA_REDIS);

    private JedisPooled jedis;

    @BeforeEach
    void conectar() {
        jedis = new JedisPooled(redis.getHost(), redis.getMappedPort(PORTA_REDIS));
        jedis.flushAll();
    }

    @AfterEach
    void desconectar() {
        jedis.close();
    }

    @Test
    @DisplayName("SET e GET: guarda e recupera o estoque em cache")
    void guardaERecuperaValor() {
        jedis.set("estoque:1", "10");

        assertEquals("10", jedis.get("estoque:1"));
        assertNull(jedis.get("estoque:inexistente"));
    }

    @Test
    @DisplayName("TTL: chave com expiração recebe tempo de vida positivo")
    void chaveComExpiracao() {
        jedis.setex("sessao:abc", 60, "usuario-1");

        long ttl = jedis.ttl("sessao:abc");
        assertTrue(ttl > 0 && ttl <= 60, "ttl = " + ttl);
    }

    @Test
    @DisplayName("INCR/DECR: contador atômico de reservas")
    void contadorAtomico() {
        jedis.set("reservas:1", "5");

        assertEquals(6L, jedis.incr("reservas:1"));
        assertEquals(4L, jedis.decrBy("reservas:1", 2));
    }
}
