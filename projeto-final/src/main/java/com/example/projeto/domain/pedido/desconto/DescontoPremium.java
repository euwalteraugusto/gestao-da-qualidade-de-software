package com.example.projeto.domain.pedido.desconto;

import com.example.projeto.domain.pedido.TipoCliente;
import org.springframework.stereotype.Component;

/** Strategy concreta: cliente PREMIUM tem 20% de desconto. */
@Component
public class DescontoPremium extends DescontoPercentual {

    public DescontoPremium() {
        super(TipoCliente.PREMIUM, "0.8");
    }
}
