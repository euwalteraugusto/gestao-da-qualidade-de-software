package com.example.projeto.interfaces.rest;

import com.example.projeto.domain.model.Produto;
import com.example.projeto.domain.service.ProdutoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Teste unitario do controller com Mockito (sem contexto Spring).
 */
@ExtendWith(MockitoExtension.class)
class ProdutoControllerTest {

    @Mock
    ProdutoService service;

    @InjectMocks
    ProdutoController controller;

    @Test
    void deveChamarServiceAoListar() {
        controller.listar();
        verify(service).listarTodos();
    }

    @Test
    void deveConverterRequestEmEntidadeAoCriar() {
        when(service.cadastrar(any(Produto.class))).thenAnswer(inv -> inv.getArgument(0));

        Produto criado = controller.criar(new ProdutoRequest("  Monitor  ", new BigDecimal("899.90")));

        assertEquals("Monitor", criado.getNome());
        assertEquals(new BigDecimal("899.90"), criado.getPreco());
    }
}
