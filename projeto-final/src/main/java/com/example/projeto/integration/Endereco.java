package com.example.projeto.integration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Endereco retornado pela API publica ViaCEP (https://viacep.com.br).
 * {@code erro = true} indica CEP com formato valido mas inexistente.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record Endereco(String cep,
                       String logradouro,
                       String complemento,
                       String bairro,
                       String localidade,
                       String uf,
                       Boolean erro) {
}
