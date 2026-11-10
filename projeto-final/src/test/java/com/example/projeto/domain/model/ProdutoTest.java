package com.example.projeto.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Melhoria apontada pela analise estatica (aula 15): possivel NullPointerException
 * em {@link Produto#getPrecoFormatado()}.
 */
class ProdutoTest {

    @Test
    void deveFormatarPrecoComDuasCasasArredondandoHalfUp() {
        Produto p = new Produto("Cabo", new BigDecimal("10.005"));
        assertEquals(new BigDecimal("10.01"), p.getPrecoFormatado());
    }

    @Test
    void naoDeveLancarNullPointerQuandoPrecoNulo() {
        Produto p = new Produto("Sem preco");
        assertEquals(new BigDecimal("0.00"), p.getPrecoFormatado());
    }
}
