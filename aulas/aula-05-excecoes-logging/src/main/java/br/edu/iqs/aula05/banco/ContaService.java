package br.edu.iqs.aula05.banco;

import java.math.BigDecimal;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Servico bancario simplificado (uma conta com saldo).
 * <p>
 * Correcoes em relacao ao slide da aula:
 * <ul>
 *   <li>usa {@link BigDecimal} em vez de {@code double} (dinheiro nao deve usar ponto flutuante);</li>
 *   <li>{@link #transferir} NAO faz "catch, loga e relanca" (anti-padrao que o proprio slide manda
 *       evitar): a excecao sobe sem ser capturada e e registrada uma unica vez, na fronteira
 *       da aplicacao (veja {@code Aplicacao}).</li>
 * </ul>
 */
public class ContaService {

    /** Regra de negocio: maximo de R$ 1.000,00 por saque. */
    public static final BigDecimal LIMITE_SAQUE = new BigDecimal("1000.00");

    private static final Logger logger = LogManager.getLogger(ContaService.class);

    private final String titular;
    private BigDecimal saldo;

    public ContaService(String titular, BigDecimal saldoInicial) {
        if (saldoInicial == null || saldoInicial.signum() < 0) {
            throw new IllegalArgumentException("Saldo inicial não pode ser nulo nem negativo");
        }
        this.titular = Objects.requireNonNull(titular, "titular");
        this.saldo = saldoInicial;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public String getTitular() {
        return titular;
    }

    /**
     * Saca um valor da conta.
     * <p>
     * Ordem das validacoes: (1) valor valido; (2) limite por saque; (3) saldo. O limite vem antes
     * do saldo porque e uma regra do produto, independente do estado da conta.
     *
     * @throws IllegalArgumentException      se o valor for nulo, zero ou negativo
     * @throws LimiteSaqueExcedidoException  se o valor for maior que {@link #LIMITE_SAQUE}
     * @throws SaldoInsuficienteException    se o valor for maior que o saldo
     */
    public void sacar(BigDecimal valor) {
        validarValorPositivo(valor);
        if (valor.compareTo(LIMITE_SAQUE) > 0) {
            throw new LimiteSaqueExcedidoException(valor, LIMITE_SAQUE);
        }
        if (valor.compareTo(saldo) > 0) {
            throw new SaldoInsuficienteException(saldo);
        }
        saldo = saldo.subtract(valor);
        logger.info("Saque de {} realizado na conta de {}. Saldo atual: {}", valor, titular, saldo);
    }

    public void depositar(BigDecimal valor) {
        validarValorPositivo(valor);
        saldo = saldo.add(valor);
        logger.info("Depósito de {} realizado na conta de {}. Saldo atual: {}", valor, titular, saldo);
    }

    /**
     * Transfere valor desta conta para outra. Se o saque falhar, a excecao de negocio simplesmente
     * propaga (nada foi alterado em nenhuma das contas) - sem catch-log-throw.
     */
    public void transferir(ContaService destino, BigDecimal valor) {
        Objects.requireNonNull(destino, "destino");
        if (destino == this) {
            throw new IllegalArgumentException("Conta de origem e destino devem ser diferentes");
        }
        logger.info("Iniciando transferência de {} de {} para {}", valor, titular, destino.titular);
        sacar(valor);
        destino.depositar(valor);
        logger.info("Transferência concluída");
    }

    private static void validarValorPositivo(BigDecimal valor) {
        if (valor == null || valor.signum() <= 0) {
            throw new IllegalArgumentException("O valor deve ser maior que zero");
        }
    }
}
