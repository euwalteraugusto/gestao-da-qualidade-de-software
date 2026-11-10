package com.example.projeto.web;

import com.example.projeto.domain.model.Produto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Objeto de formulario (form backing bean) do cadastro de produto na interface web.
 * Classe mutavel (getters/setters) porque o Thymeleaf faz o binding campo a campo.
 */
@Getter
@Setter
public class ProdutoForm {

    @NotBlank(message = "Informe o nome do produto")
    @Size(max = 120, message = "O nome deve ter no maximo 120 caracteres")
    private String nome;

    @NotNull(message = "Informe o preco")
    @Positive(message = "O preco deve ser maior que zero")
    private BigDecimal preco;

    public Produto toEntity() {
        return new Produto(nome.trim(), preco);
    }
}
