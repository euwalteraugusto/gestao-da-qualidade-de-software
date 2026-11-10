package br.edu.iqs.aula05.banco;

import java.math.BigDecimal;

/**
 * Erro de negocio: o valor pedido para saque e maior que o saldo disponivel.
 * <p>
 * Herda de {@link RuntimeException} (unchecked): e uma violacao de regra de negocio que o
 * chamador normalmente nao consegue "consertar" no ponto da chamada; a camada superior
 * (ex.: controller/tela) traduz para uma mensagem ao usuario.
 */
public class SaldoInsuficienteException extends RuntimeException {

    private final BigDecimal saldoAtual;

    public SaldoInsuficienteException(BigDecimal saldoAtual) {
        super("Saldo insuficiente. Disponível: " + FormatoMonetario.formatar(saldoAtual));
        this.saldoAtual = saldoAtual;
    }

    /** Saldo que estava disponivel no momento da tentativa de saque. */
    public BigDecimal getSaldoAtual() {
        return saldoAtual;
    }
}
