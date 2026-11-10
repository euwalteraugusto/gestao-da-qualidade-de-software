package com.example.projeto.domain.service;

import com.example.projeto.domain.model.Produto;
import com.example.projeto.domain.repository.ProdutoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Testes unitarios do servico (Mockito, sem Spring) - piramide de testes: base larga e rapida.
 */
@ExtendWith(MockitoExtension.class)
class ProdutoServiceTest {

    @Mock
    ProdutoRepository repository;

    @InjectMocks
    ProdutoService service;

    @Test
    void deveBuscarProdutosPorNome() {
        when(repository.findByNomeContainingIgnoreCase("Xiaomi"))
                .thenReturn(List.of(new Produto("Smartphone Xiaomi")));

        List<Produto> result = service.buscarPorNome("Xiaomi");

        assertEquals(1, result.size());
        verify(repository).findByNomeContainingIgnoreCase("Xiaomi");
    }

    @Test
    void deveListarTodos() {
        when(repository.findAll()).thenReturn(List.of(new Produto("A"), new Produto("B")));
        assertEquals(2, service.listarTodos().size());
    }

    @Test
    void deveLancarExcecaoQuandoProdutoNaoExiste() {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        ProdutoNaoEncontradoException ex =
                assertThrows(ProdutoNaoEncontradoException.class, () -> service.buscarPorId(99L));
        assertEquals("Produto nao encontrado: id=99", ex.getMessage());
    }

    @Test
    void deveIgnorarIdInformadoAoCadastrar() {
        Produto novo = new Produto("Teclado", new BigDecimal("150.00"));
        novo.setId(42L);
        when(repository.save(any(Produto.class))).thenAnswer(inv -> inv.getArgument(0));

        Produto salvo = service.cadastrar(novo);

        assertNull(salvo.getId());
    }

    @Test
    void deveAtualizarProdutoExistente() {
        Produto existente = new Produto("Mouse", new BigDecimal("50.00"));
        existente.setId(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(repository.save(existente)).thenReturn(existente);

        Produto atualizado = service.atualizar(1L, new Produto("Mouse Gamer", new BigDecimal("80.00")));

        assertEquals("Mouse Gamer", atualizado.getNome());
        assertEquals(new BigDecimal("80.00"), atualizado.getPreco());
    }

    @Test
    void naoDeveRemoverProdutoInexistente() {
        when(repository.existsById(7L)).thenReturn(false);
        assertThrows(ProdutoNaoEncontradoException.class, () -> service.remover(7L));
        verify(repository, never()).deleteById(any());
    }

    @Test
    void deveRemoverProdutoExistente() {
        when(repository.existsById(7L)).thenReturn(true);
        service.remover(7L);
        verify(repository).deleteById(7L);
    }
}
