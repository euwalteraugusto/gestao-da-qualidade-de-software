package com.example.projeto.interfaces.rest;

import com.example.projeto.domain.repository.ProdutoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

/**
 * Teste unitario do controller com Mockito (sem contexto Spring).
 */
@ExtendWith(MockitoExtension.class)
class ProdutoControllerTest {

    @Mock
    ProdutoRepository repository;

    @InjectMocks
    ProdutoController controller;

    @Test
    void deveChamarRepositoryAoListar() {
        controller.listar();
        verify(repository).findAll();
    }
}
