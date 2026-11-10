package com.example.projeto.interfaces.rest;

import com.example.projeto.domain.pedido.ItemPedido;
import com.example.projeto.domain.pedido.Pedido;
import com.example.projeto.domain.pedido.PedidoService;
import com.example.projeto.domain.pedido.ResumoPedido;
import com.example.projeto.domain.service.ProdutoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * US-003: calcula subtotal, desconto por tipo de cliente, frete e total de um pedido.
 */
@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;
    private final ProdutoService produtoService;

    @PostMapping("/total")
    public ResumoPedido calcularTotal(@Valid @RequestBody PedidoRequest request) {
        Pedido pedido = new Pedido(request.itens().stream()
                .map(item -> new ItemPedido(produtoService.buscarPorId(item.produtoId()).getPrecoFormatado(),
                        item.quantidade()))
                .toList());
        return pedidoService.calcularTotal(pedido, request.tipoCliente());
    }
}
