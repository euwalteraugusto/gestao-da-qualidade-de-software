package com.example.projeto.domain.pedido;

import com.example.projeto.domain.pedido.desconto.PoliticaDesconto;
import com.example.projeto.domain.pedido.desconto.PoliticaDescontoFactory;
import com.example.projeto.domain.pedido.frete.CalculadoraFrete;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Calcula o total de um pedido (US-003) - versao refatorada na aula 16.
 * <p>
 * O codigo legado concentrava 3 responsabilidades num metodo longo com {@code switch}
 * (Long Method + Switch Statement). Agora: o subtotal e calculado pelos proprios itens
 * (Move Method), o desconto e delegado a uma <b>Strategy</b> obtida pela <b>Factory</b>,
 * e o frete a uma {@link CalculadoraFrete} (decorada com log - <b>Decorator</b>).
 */
@Service
public class PedidoService {

    private final PoliticaDescontoFactory descontos;
    private final CalculadoraFrete calculadoraFrete;

    public PedidoService(PoliticaDescontoFactory descontos, CalculadoraFrete calculadoraFrete) {
        this.descontos = descontos;
        this.calculadoraFrete = calculadoraFrete;
    }

    public ResumoPedido calcularTotal(Pedido pedido, TipoCliente tipoCliente) {
        BigDecimal subtotal = pedido.subtotal();
        PoliticaDesconto politica = descontos.obter(tipoCliente);
        BigDecimal comDesconto = politica.aplicarDesconto(subtotal);
        BigDecimal frete = calculadoraFrete.calcular(comDesconto);
        return new ResumoPedido(politica.tipoCliente(), subtotal, subtotal.subtract(comDesconto),
                frete, comDesconto.add(frete));
    }
}
