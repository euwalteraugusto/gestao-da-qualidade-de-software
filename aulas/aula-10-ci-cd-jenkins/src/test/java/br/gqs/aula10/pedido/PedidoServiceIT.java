package br.gqs.aula10.pedido;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;

/** Integração entre as peças reais (serviço + regra + repositório), sem mocks. Roda com o Failsafe. */
class PedidoServiceIT {

    @Test
    void fluxoCompletoDeFechamentoEConsulta() {
        RepositorioPedidos repositorio = new RepositorioMemoria();
        PedidoService service = new PedidoService(new CalculadoraDesconto(), repositorio);

        service.fechar("IT-1", List.of(
                new ItemPedido("A", 1, new BigDecimal("800.00")),
                new ItemPedido("B", 1, new BigDecimal("400.00"))));

        Pedido recuperado = repositorio.buscar("IT-1").orElseThrow();
        assertEquals(new BigDecimal("1080.00"), recuperado.total());
        assertEquals(2, recuperado.itens().size());
    }
}
