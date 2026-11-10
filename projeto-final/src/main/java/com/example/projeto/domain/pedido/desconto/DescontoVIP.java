package com.example.projeto.domain.pedido.desconto;

import com.example.projeto.domain.pedido.TipoCliente;
import org.springframework.stereotype.Component;

/** Strategy concreta: cliente VIP tem 10% de desconto. */
@Component
public class DescontoVIP extends DescontoPercentual {

    public DescontoVIP() {
        super(TipoCliente.VIP, "0.9");
    }
}
