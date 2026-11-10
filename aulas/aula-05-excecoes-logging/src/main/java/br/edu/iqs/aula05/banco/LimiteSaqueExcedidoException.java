package br.edu.iqs.aula05.banco;

import java.math.BigDecimal;

/**
 * Erro de negocio: o valor pedido ultrapassa o limite maximo permitido por saque
 * (R$ 1.000,00 em {@link ContaService#LIMITE_SAQUE}).
 */
public class LimiteSaqueExcedidoException extends RuntimeException {

    private final BigDecimal valorSolicitado;
    private final BigDecimal limite;

    public LimiteSaqueExcedidoException(BigDecimal valorSolicitado, BigDecimal limite) {
        super("Limite de saque excedido. Máximo por saque: " + FormatoMonetario.formatar(limite)
                + "; solicitado: " + FormatoMonetario.formatar(valorSolicitado));
        this.valorSolicitado = valorSolicitado;
        this.limite = limite;
    }

    public BigDecimal getValorSolicitado() {
        return valorSolicitado;
    }

    public BigDecimal getLimite() {
        return limite;
    }
}
