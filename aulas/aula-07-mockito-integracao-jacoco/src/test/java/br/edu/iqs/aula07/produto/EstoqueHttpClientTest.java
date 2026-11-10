package br.edu.iqs.aula07.produto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.URI;
import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.sun.net.httpserver.HttpServer;

/**
 * Exercicio do JaCoCo ("identifique uma classe com baixa cobertura e implemente testes"):
 * {@link EstoqueHttpClient} aparecia com 0% no relatorio inicial. Estes testes a exercitam
 * contra um servidor HTTP REAL em memoria (com.sun.net.httpserver), sem mocks e sem Docker.
 */
@DisplayName("EstoqueHttpClient (contra servidor HTTP local)")
class EstoqueHttpClientTest {

    private HttpServer servidor;
    private EstoqueHttpClient cliente;
    private final AtomicReference<String> metodoRecebido = new AtomicReference<>();
    private final AtomicReference<String> caminhoRecebido = new AtomicReference<>();
    private final AtomicReference<String> corpoRecebido = new AtomicReference<>();

    @BeforeEach
    void iniciarServidor() throws IOException {
        servidor = HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0), 0);
        servidor.start();
        cliente = new EstoqueHttpClient(HttpClient.newHttpClient(), baseUrl());
    }

    @AfterEach
    void pararServidor() {
        servidor.stop(0);
    }

    private URI baseUrl() {
        return URI.create("http://" + InetAddress.getLoopbackAddress().getHostAddress() + ":"
                + servidor.getAddress().getPort());
    }

    /** Registra um handler para /estoque que responde com o status e o corpo informados. */
    private void responder(int status, String corpo) {
        servidor.createContext("/estoque", troca -> {
            metodoRecebido.set(troca.getRequestMethod());
            caminhoRecebido.set(troca.getRequestURI().getPath());
            corpoRecebido.set(new String(troca.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
            byte[] bytes = corpo.getBytes(StandardCharsets.UTF_8);
            troca.sendResponseHeaders(status, bytes.length == 0 ? -1 : bytes.length);
            if (bytes.length > 0) {
                troca.getResponseBody().write(bytes);
            }
            troca.close();
        });
    }

    @Test
    @DisplayName("200: devolve o estoque restante e envia POST /estoque/{id}/reservas com a quantidade")
    void reservaComSucesso() {
        responder(200, "7\n");

        int restante = cliente.reservar(42L, 3);

        assertEquals(7, restante);
        assertEquals("POST", metodoRecebido.get());
        assertEquals("/estoque/42/reservas", caminhoRecebido.get());
        assertEquals("3", corpoRecebido.get());
    }

    @Test
    @DisplayName("409: vira EstoqueInsuficienteException")
    void conflitoViraEstoqueInsuficiente() {
        responder(409, "");

        assertThrows(EstoqueInsuficienteException.class, () -> cliente.reservar(1L, 100));
    }

    @Test
    @DisplayName("500: vira EstoqueIndisponivelException com o status na mensagem")
    void erroDoServidorViraIndisponivel() {
        responder(500, "erro interno");

        EstoqueIndisponivelException e = assertThrows(EstoqueIndisponivelException.class,
                () -> cliente.reservar(1L, 1));

        assertTrue(e.getMessage().contains("500"), e.getMessage());
    }

    @Test
    @DisplayName("200 com corpo que não é número: EstoqueIndisponivelException com a causa preservada")
    void corpoInvalido() {
        responder(200, "nao-e-numero");

        EstoqueIndisponivelException e = assertThrows(EstoqueIndisponivelException.class,
                () -> cliente.reservar(1L, 1));

        assertInstanceOf(NumberFormatException.class, e.getCause());
    }

    @Test
    @DisplayName("Servidor fora do ar (conexão recusada): EstoqueIndisponivelException")
    void servidorForaDoAr() throws IOException {
        int portaFechada;
        try (ServerSocket socket = new ServerSocket(0, 0, InetAddress.getLoopbackAddress())) {
            portaFechada = socket.getLocalPort(); // porta livre; o socket e fechado logo a seguir
        }
        EstoqueHttpClient semServidor = new EstoqueHttpClient(HttpClient.newHttpClient(),
                URI.create("http://" + InetAddress.getLoopbackAddress().getHostAddress() + ":" + portaFechada));

        EstoqueIndisponivelException e = assertThrows(EstoqueIndisponivelException.class,
                () -> semServidor.reservar(1L, 1));

        assertInstanceOf(IOException.class, e.getCause());
    }

    @Test
    @DisplayName("Thread interrompida: EstoqueIndisponivelException e o flag de interrupção é restaurado")
    void threadInterrompida() {
        responder(200, "1");
        Thread.currentThread().interrupt();
        try {
            EstoqueIndisponivelException e = assertThrows(EstoqueIndisponivelException.class,
                    () -> cliente.reservar(1L, 1));
            assertInstanceOf(InterruptedException.class, e.getCause());
            assertTrue(Thread.currentThread().isInterrupted(), "flag de interrupção deve ser restaurado");
        } finally {
            Thread.interrupted(); // limpa o flag para nao contaminar outros testes
        }
    }

    @Test
    @DisplayName("Construtor rejeita argumentos nulos; exceções têm construtores com mensagem")
    void argumentosNulosEConstrutores() {
        assertThrows(NullPointerException.class, () -> new EstoqueHttpClient(null, baseUrl()));
        assertThrows(NullPointerException.class, () -> new EstoqueHttpClient(HttpClient.newHttpClient(), null));
        assertEquals("sem saldo", new EstoqueInsuficienteException("sem saldo").getMessage());
        assertEquals("Estoque insuficiente", new EstoqueInsuficienteException().getMessage());
    }
}
