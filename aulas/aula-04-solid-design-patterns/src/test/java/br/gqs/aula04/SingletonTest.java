package br.gqs.aula04;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.gqs.aula04.singleton.DatabaseConnection;
import br.gqs.aula04.singleton.Logger;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Atividade 2: Singleton (DatabaseConnection do slide e Logger do exercício). */
class SingletonTest {

    @BeforeEach
    void limparLogger() {
        Logger.getInstance().limpar();
    }

    @Test
    void databaseConnectionRetornaSempreAMesmaInstancia() {
        assertSame(DatabaseConnection.getInstance(), DatabaseConnection.getInstance());
    }

    @Test
    void construtoresSaoPrivados() {
        for (Class<?> tipo : List.of(DatabaseConnection.class, Logger.class)) {
            for (Constructor<?> c : tipo.getDeclaredConstructors()) {
                assertTrue(Modifier.isPrivate(c.getModifiers()), tipo.getSimpleName());
            }
        }
    }

    @Test
    void databaseConnectionMantemEstadoCompartilhado() {
        DatabaseConnection a = DatabaseConnection.getInstance();
        a.disconnect();
        assertFalse(DatabaseConnection.getInstance().isConnected());
        a.connect();
        assertTrue(DatabaseConnection.getInstance().isConnected());
        a.disconnect();
    }

    @Test
    void getInstanceEThreadSafeSob200ThreadsConcorrentes() throws Exception {
        verificarInstanciaUnicaConcorrente(DatabaseConnection::getInstance);
    }

    @Test
    void loggerEThreadSafeSob200ThreadsConcorrentes() throws Exception {
        verificarInstanciaUnicaConcorrente(Logger::getInstance);
    }

    private static <T> void verificarInstanciaUnicaConcorrente(Callable<T> obtencao) throws Exception {
        int threads = 200;
        ExecutorService pool = Executors.newFixedThreadPool(32);
        CountDownLatch largada = new CountDownLatch(1);
        Set<T> instancias = ConcurrentHashMap.newKeySet();
        List<Future<?>> futuros = new ArrayList<>();
        for (int i = 0; i < threads; i++) {
            futuros.add(pool.submit(() -> {
                largada.await();
                instancias.add(obtencao.call());
                return null;
            }));
        }
        largada.countDown();
        for (Future<?> f : futuros) {
            f.get();
        }
        pool.shutdown();
        assertEquals(1, instancias.size());
    }

    @Test
    void loggerRegistraMensagensComNivel() {
        Logger log = Logger.getInstance();
        log.info("iniciando");
        log.warn("atenção");
        log.error("falhou");
        assertEquals(List.of("[INFO] iniciando", "[WARN] atenção", "[ERROR] falhou"), log.entradas());
    }

    @Test
    void loggerCompartilhaMensagensEntreReferencias() {
        Logger.getInstance().info("a");
        assertEquals(List.of("[INFO] a"), Logger.getInstance().entradas());
    }

    @Test
    void loggerNaoAceitaEntradasNulas() {
        assertThrows(IllegalArgumentException.class, () -> Logger.getInstance().info(null));
        assertThrows(IllegalArgumentException.class, () -> Logger.getInstance().log(null, "x"));
    }

    @Test
    void entradasDevolveCopiaImutavel() {
        Logger.getInstance().info("x");
        List<String> copia = Logger.getInstance().entradas();
        assertThrows(UnsupportedOperationException.class, () -> copia.add("y"));
    }

    @Test
    void loggerSuportaEscritaConcorrenteSemPerderMensagens() throws Exception {
        int threads = 50;
        ExecutorService pool = Executors.newFixedThreadPool(16);
        List<Future<?>> futuros = new ArrayList<>();
        for (int i = 0; i < threads; i++) {
            final int n = i;
            futuros.add(pool.submit(() -> Logger.getInstance().info("m" + n)));
        }
        for (Future<?> f : futuros) {
            f.get();
        }
        pool.shutdown();
        assertEquals(threads, Logger.getInstance().entradas().size());
    }
}
