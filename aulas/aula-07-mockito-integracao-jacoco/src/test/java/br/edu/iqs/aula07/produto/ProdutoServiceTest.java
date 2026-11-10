package br.edu.iqs.aula07.produto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Desafio integrado: ProdutoService com ProdutoRepository e EstoqueClient (chamada HTTP) mockados.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ProdutoService (repositório e cliente de estoque mockados)")
class ProdutoServiceTest {

    private static final Produto ATIVO = new Produto(1L, "Notebook", true);

    @Mock
    ProdutoRepository produtoRepository;

    @Mock
    EstoqueClient estoqueClient; // mock para o HTTP client

    @InjectMocks
    ProdutoService service;

    @Test
    @DisplayName("reservarEstoque chama EstoqueClient.reservar e devolve o estoque restante")
    void deveChamarEstoqueClientAoReservar() {
        when(produtoRepository.buscarPorId(1L)).thenReturn(Optional.of(ATIVO));
        when(estoqueClient.reservar(1L, 3)).thenReturn(7);

        int restante = service.reservarEstoque(1L, 3);

        assertEquals(7, restante);
        verify(estoqueClient).reservar(1L, 3);
        verifyNoMoreInteractions(estoqueClient);
    }

    @Test
    @DisplayName("Estoque insuficiente: a exceção do cliente propaga (exemplo do slide)")
    void deveFalharQuandoEstoqueInsuficiente() {
        when(produtoRepository.buscarPorId(1L)).thenReturn(Optional.of(ATIVO));
        when(estoqueClient.reservar(anyLong(), anyInt()))
                .thenThrow(new EstoqueInsuficienteException());

        assertThrows(EstoqueInsuficienteException.class,
                () -> service.reservarEstoque(1L, 100));

        verify(estoqueClient).reservar(1L, 100);
    }

    @Test
    @DisplayName("Falha de comunicação com o estoque propaga como EstoqueIndisponivelException")
    void devePropagarFalhaDeComunicacao() {
        when(produtoRepository.buscarPorId(1L)).thenReturn(Optional.of(ATIVO));
        when(estoqueClient.reservar(1L, 1)).thenThrow(new EstoqueIndisponivelException("timeout"));

        assertThrows(EstoqueIndisponivelException.class, () -> service.reservarEstoque(1L, 1));
    }

    @Test
    @DisplayName("Produto inexistente: lança ProdutoNaoEncontradoException e NÃO chama o estoque")
    void naoDeveChamarEstoqueQuandoProdutoNaoExiste() {
        when(produtoRepository.buscarPorId(42L)).thenReturn(Optional.empty());

        ProdutoNaoEncontradoException e = assertThrows(ProdutoNaoEncontradoException.class,
                () -> service.reservarEstoque(42L, 1));

        assertTrue(e.getMessage().contains("42"));
        verifyNoInteractions(estoqueClient);
    }

    @Test
    @DisplayName("Produto inativo: lança IllegalStateException e NÃO chama o estoque")
    void naoDeveReservarProdutoInativo() {
        when(produtoRepository.buscarPorId(2L)).thenReturn(Optional.of(new Produto(2L, "Antigo", false)));

        assertThrows(IllegalStateException.class, () -> service.reservarEstoque(2L, 1));

        verify(estoqueClient, never()).reservar(anyLong(), anyInt());
    }

    @ParameterizedTest(name = "quantidade {0} é inválida")
    @ValueSource(ints = {0, -1, Integer.MIN_VALUE})
    @DisplayName("Quantidade menor ou igual a zero é rejeitada antes de qualquer chamada")
    void quantidadeInvalida(int quantidade) {
        assertThrows(IllegalArgumentException.class, () -> service.reservarEstoque(1L, quantidade));
        verifyNoInteractions(produtoRepository, estoqueClient);
    }

    @Test
    @DisplayName("Dependências nulas no construtor são rejeitadas")
    void construtorRejeitaNulos() {
        assertThrows(NullPointerException.class, () -> new ProdutoService(null, estoqueClient));
        assertThrows(NullPointerException.class, () -> new ProdutoService(produtoRepository, null));
    }
}
