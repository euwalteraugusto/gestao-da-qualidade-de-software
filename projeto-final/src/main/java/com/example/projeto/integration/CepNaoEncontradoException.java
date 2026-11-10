package com.example.projeto.integration;

/**
 * CEP com formato valido, mas inexistente na base do ViaCEP.
 */
public class CepNaoEncontradoException extends RuntimeException {

    public CepNaoEncontradoException(String cep) {
        super("CEP nao encontrado: " + cep);
    }
}
