package br.edu.iqs.aula05;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.Property;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import br.edu.iqs.aula05.banco.ContaService;
import br.edu.iqs.aula05.banco.SaldoInsuficienteException;

/** Verifica a configuracao do Log4j2 (FileAppender) e a ausencia de catch-log-throw. */
class LoggingTest {

    /** Appender de teste que apenas guarda os eventos em memoria. */
    private static final class CapturaAppender extends AbstractAppender {
        final List<LogEvent> eventos = new CopyOnWriteArrayList<>();

        CapturaAppender() {
            super("captura", null, null, true, Property.EMPTY_ARRAY);
        }

        @Override
        public void append(LogEvent evento) {
            eventos.add(evento.toImmutable());
        }
    }

    @Test
    @DisplayName("FileAppender grava a mensagem no arquivo aula05.log")
    void fileAppenderGravaEmArquivo() throws IOException {
        Logger logger = LogManager.getLogger("teste.arquivo");
        String marcador = "marcador-" + UUID.randomUUID();

        logger.info("Mensagem de teste {}", marcador);

        // O Maven define -Dlog.dir=target/logs; fora do Maven o padrao do log4j2.xml e ./logs
        Path arquivo = Path.of(System.getProperty("log.dir", "logs"), "aula05.log");
        assertTrue(Files.exists(arquivo), "arquivo de log deveria existir: " + arquivo);
        String conteudo = Files.readString(arquivo, StandardCharsets.UTF_8);
        assertTrue(conteudo.contains(marcador), "mensagem nao encontrada no arquivo");
        assertTrue(conteudo.contains("INFO"), "nivel deveria aparecer no padrao");
        assertTrue(conteudo.contains("teste.arquivo"), "nome do logger deveria aparecer");
    }

    @Test
    @DisplayName("Falha de transferência NÃO gera log ERROR nas camadas internas (sem catch-log-throw)")
    void semCatchLogThrow() {
        LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        Configuration config = ctx.getConfiguration();
        CapturaAppender captura = new CapturaAppender();
        captura.start();
        config.addAppender(captura);
        config.getRootLogger().addAppender(captura, Level.ALL, null);
        ctx.updateLoggers();
        try {
            ContaService origem = new ContaService("Ana", new BigDecimal("100.00"));
            ContaService destino = new ContaService("Bruno", BigDecimal.ZERO);

            try {
                origem.transferir(destino, new BigDecimal("500.00"));
            } catch (SaldoInsuficienteException esperada) {
                // a excecao chega ao chamador sem ter sido registrada como ERROR antes
            }

            long erros = captura.eventos.stream().filter(e -> e.getLevel().isMoreSpecificThan(Level.ERROR)).count();
            assertEquals(0, erros, "o servico nao deve registrar ERROR e relancar a excecao");
            assertTrue(captura.eventos.stream().anyMatch(e -> e.getLevel() == Level.INFO
                            && e.getMessage().getFormattedMessage().contains("Iniciando transferência")),
                    "deveria registrar o INFO de inicio da transferencia");
        } finally {
            config.getRootLogger().removeAppender("captura");
            captura.stop();
            ctx.updateLoggers();
        }
    }

    @Test
    @DisplayName("Níveis: DEBUG < INFO < WARN < ERROR < FATAL")
    void ordemDosNiveis() {
        assertTrue(Level.DEBUG.isLessSpecificThan(Level.INFO));
        assertTrue(Level.INFO.isLessSpecificThan(Level.WARN));
        assertTrue(Level.WARN.isLessSpecificThan(Level.ERROR));
        assertTrue(Level.ERROR.isLessSpecificThan(Level.FATAL));
    }
}
