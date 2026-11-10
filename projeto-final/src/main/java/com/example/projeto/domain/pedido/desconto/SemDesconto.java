package com.example.projeto.domain.pedido.desconto;

import com.example.projeto.domain.pedido.TipoCliente;
import org.springframework.stereotype.Component;

/** Strategy concreta (Null Object): cliente REGULAR nao tem desconto. */
@Component
public class SemDesconto extends DescontoPercentual {

    public SemDesconto() {
        super(TipoCliente.REGULAR, "1");
    }
}
