package br.gqs.aula10;

import br.gqs.aula10.pedido.CalculadoraDesconto;
import br.gqs.aula10.pedido.ItemPedido;
import br.gqs.aula10.pedido.Pedido;
import br.gqs.aula10.pedido.PedidoService;
import br.gqs.aula10.pedido.RepositorioMemoria;
import java.math.BigDecimal;
import java.util.List;

/** Ponto de entrada usado pelo Dockerfile: fecha um pedido de exemplo e imprime o total. */
public final class Aplicacao {

    private Aplicacao() {
    }

    /**
     * Executa a demonstração.
     *
     * @param args não utilizado
     */
    public static void main(String[] args) {
        PedidoService service = new PedidoService(new CalculadoraDesconto(), new RepositorioMemoria());
        Pedido pedido = service.fechar("PED-1", List.of(new ItemPedido("LIVRO-JAVA", 2, new BigDecimal("300.00"))));
        System.out.println("Pedido " + pedido.id() + " fechado. Total: " + pedido.total());
    }
}
