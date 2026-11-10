package br.edu.iqs.aula07.produto;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Objects;

/**
 * Implementacao real de {@link EstoqueClient} usando o {@code java.net.http.HttpClient} do JDK.
 * <p>
 * Contrato do servico de estoque (fictício): {@code POST {base}/estoque/{id}/reservas} com corpo
 * texto {@code quantidade}; {@code 200} devolve o estoque restante no corpo; {@code 409} indica
 * estoque insuficiente; qualquer outro status e falha do servico.
 */
public class EstoqueHttpClient implements EstoqueClient {

    private final HttpClient httpClient;
    private final URI baseUrl;

    public EstoqueHttpClient(HttpClient httpClient, URI baseUrl) {
        this.httpClient = Objects.requireNonNull(httpClient, "httpClient");
        this.baseUrl = Objects.requireNonNull(baseUrl, "baseUrl");
    }

    @Override
    public int reservar(long produtoId, int quantidade) {
        HttpRequest requisicao = HttpRequest.newBuilder(baseUrl.resolve("/estoque/" + produtoId + "/reservas"))
                .timeout(Duration.ofSeconds(5))
                .header("Content-Type", "text/plain; charset=UTF-8")
                .POST(HttpRequest.BodyPublishers.ofString(Integer.toString(quantidade)))
                .build();
        HttpResponse<String> resposta;
        try {
            resposta = httpClient.send(requisicao, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new EstoqueIndisponivelException("Falha de comunicação com o serviço de estoque", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new EstoqueIndisponivelException("Chamada ao serviço de estoque interrompida", e);
        }
        return interpretar(resposta);
    }

    private static int interpretar(HttpResponse<String> resposta) {
        int status = resposta.statusCode();
        if (status == 409) {
            throw new EstoqueInsuficienteException();
        }
        if (status != 200) {
            throw new EstoqueIndisponivelException("Serviço de estoque respondeu HTTP " + status);
        }
        try {
            return Integer.parseInt(resposta.body().trim());
        } catch (NumberFormatException e) {
            throw new EstoqueIndisponivelException("Resposta inválida do serviço de estoque: " + resposta.body(), e);
        }
    }
}
