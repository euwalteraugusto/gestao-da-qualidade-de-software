package com.example.projeto.integration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.regex.Pattern;

/**
 * Consome a API externa ViaCEP com {@link RestTemplate}.
 * <p>
 * Diferencas em relacao ao slide: o {@code RestTemplate} e criado pelo
 * {@link RestTemplateBuilder} (com timeouts - uma API lenta nao pode prender as threads
 * do servidor) e o CEP e validado e passado como variavel de URI, em vez de
 * concatenado na URL (evita injecao de caminho como {@code "../"}).
 */
@Service
public class CepService {

    private static final Pattern CEP_VALIDO = Pattern.compile("\\d{8}");

    private final RestTemplate restTemplate;
    private final String urlTemplate;

    public CepService(RestTemplateBuilder builder,
                      @Value("${app.viacep.url:https://viacep.com.br/ws}") String baseUrl,
                      @Value("${app.viacep.timeout:3s}") Duration timeout) {
        this.urlTemplate = baseUrl + "/{cep}/json/";
        this.restTemplate = builder
                .setConnectTimeout(timeout)
                .setReadTimeout(timeout)
                .build();
    }

    public Endereco buscarPorCep(String cep) {
        String normalizado = normalizar(cep);
        Endereco endereco = restTemplate.getForObject(urlTemplate, Endereco.class, normalizado);
        if (endereco == null || Boolean.TRUE.equals(endereco.erro())) {
            throw new CepNaoEncontradoException(normalizado);
        }
        return endereco;
    }

    /** Remove mascara (01001-000 -> 01001000) e exige 8 digitos. */
    static String normalizar(String cep) {
        String digitos = cep == null ? "" : cep.replace("-", "").trim();
        if (!CEP_VALIDO.matcher(digitos).matches()) {
            throw new IllegalArgumentException("CEP invalido: informe 8 digitos (ex.: 01001-000)");
        }
        return digitos;
    }
}
