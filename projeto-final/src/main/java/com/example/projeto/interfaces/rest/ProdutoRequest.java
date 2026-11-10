package com.example.projeto.interfaces.rest;

import com.example.projeto.domain.model.Produto;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * DTO de entrada da API (US-002: validacao de campos obrigatorios).
 * <p>
 * Evita receber a entidade JPA diretamente no corpo da requisicao (o cliente
 * poderia enviar o {@code id} e sobrescrever registros - "mass assignment").
 */
public record ProdutoRequest(
        @NotBlank(message = "nome e obrigatorio")
        @Size(max = 120, message = "nome deve ter no maximo 120 caracteres")
        String nome,

        @NotNull(message = "preco e obrigatorio")
        @Positive(message = "preco deve ser maior que zero")
        @Digits(integer = 8, fraction = 2, message = "preco deve ter no maximo 8 digitos inteiros e 2 decimais")
        BigDecimal preco) {

    public Produto toEntity() {
        return new Produto(nome.trim(), preco);
    }
}
