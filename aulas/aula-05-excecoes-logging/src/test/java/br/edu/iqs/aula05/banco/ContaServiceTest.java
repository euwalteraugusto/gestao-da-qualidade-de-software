package br.edu.iqs.aula05.banco;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ContaServiceTest {

    private ContaService conta;

    private static BigDecimal reais(String valor) {
        return new BigDecimal(valor);
    }

    @BeforeEach
    void criarConta() {
        conta = new ContaService("Ana", reais("500.00"));
    }

    @Test
    @DisplayName("Saque dentro do saldo e do limite reduz o saldo")
    void saqueValido() {
        conta.sacar(reais("200.00"));
        assertEquals(0, reais("300.00").compareTo(conta.getSaldo()));
    }

    @Test
    @DisplayName("Saque maior que o saldo lança SaldoInsuficienteException com o saldo disponível")
    void saldoInsuficiente() {
        SaldoInsuficienteException e = assertThrows(SaldoInsuficienteException.class,
                () -> conta.sacar(reais("600.00")));
        assertEquals(0, reais("500.00").compareTo(e.getSaldoAtual()));
        assertEquals("Saldo insuficiente. Disponível: R$ 500,00", e.getMessage());
        assertEquals(0, reais("500.00").compareTo(conta.getSaldo()), "saldo não pode mudar");
    }

    @Test
    @DisplayName("Saque acima de R$ 1000 lança LimiteSaqueExcedidoException")
    void limiteExcedido() {
        ContaService rica = new ContaService("Rico", reais("5000.00"));
        LimiteSaqueExcedidoException e = assertThrows(LimiteSaqueExcedidoException.class,
                () -> rica.sacar(reais("1000.01")));
        assertEquals(0, reais("1000.00").compareTo(e.getLimite()));
        assertEquals(0, reais("1000.01").compareTo(e.getValorSolicitado()));
        assertTrue(e.getMessage().contains("R$ 1.000,00"), e.getMessage());
        assertEquals(0, reais("5000.00").compareTo(rica.getSaldo()), "saldo não pode mudar");
    }

    @Test
    @DisplayName("Saque de exatamente R$ 1000 é permitido (limite inclusivo)")
    void saqueNoLimiteExato() {
        ContaService rica = new ContaService("Rico", reais("5000.00"));
        rica.sacar(reais("1000.00"));
        assertEquals(0, reais("4000.00").compareTo(rica.getSaldo()));
    }

    @Test
    @DisplayName("Quando excede o limite E o saldo, vale o limite (regra verificada primeiro)")
    void limiteTemPrioridadeSobreSaldo() {
        assertThrows(LimiteSaqueExcedidoException.class, () -> conta.sacar(reais("1500.00")));
    }

    @Test
    @DisplayName("Valores nulos, zero ou negativos são rejeitados")
    void valoresInvalidos() {
        assertThrows(IllegalArgumentException.class, () -> conta.sacar(null));
        assertThrows(IllegalArgumentException.class, () -> conta.sacar(BigDecimal.ZERO));
        assertThrows(IllegalArgumentException.class, () -> conta.sacar(reais("-1")));
        assertThrows(IllegalArgumentException.class, () -> conta.depositar(reais("0")));
    }

    @Test
    @DisplayName("Depósito aumenta o saldo")
    void deposito() {
        conta.depositar(reais("50.50"));
        assertEquals(0, reais("550.50").compareTo(conta.getSaldo()));
    }

    @Test
    @DisplayName("Transferência movimenta o valor entre as contas")
    void transferenciaComSucesso() {
        ContaService destino = new ContaService("Bruno", reais("100.00"));
        conta.transferir(destino, reais("200.00"));
        assertEquals(0, reais("300.00").compareTo(conta.getSaldo()));
        assertEquals(0, reais("300.00").compareTo(destino.getSaldo()));
    }

    @Test
    @DisplayName("Transferência com falha não altera nenhuma das contas")
    void transferenciaComFalhaNaoAlteraSaldos() {
        ContaService destino = new ContaService("Bruno", reais("100.00"));
        assertThrows(SaldoInsuficienteException.class, () -> conta.transferir(destino, reais("900.00")));
        assertEquals(0, reais("500.00").compareTo(conta.getSaldo()));
        assertEquals(0, reais("100.00").compareTo(destino.getSaldo()));
    }

    @Test
    @DisplayName("Transferir para a própria conta ou para destino nulo é rejeitado")
    void transferenciaInvalida() {
        assertThrows(IllegalArgumentException.class, () -> conta.transferir(conta, reais("1")));
        assertThrows(NullPointerException.class, () -> conta.transferir(null, reais("1")));
    }

    @Test
    @DisplayName("Saldo inicial negativo ou nulo é rejeitado")
    void saldoInicialInvalido() {
        assertThrows(IllegalArgumentException.class, () -> new ContaService("X", reais("-1")));
        assertThrows(IllegalArgumentException.class, () -> new ContaService("X", null));
    }

    @Test
    @DisplayName("As exceções de negócio são unchecked (RuntimeException), não checked")
    void excecoesSaoUnchecked() {
        assertInstanceOf(RuntimeException.class, new SaldoInsuficienteException(BigDecimal.ONE));
        assertInstanceOf(RuntimeException.class,
                new LimiteSaqueExcedidoException(BigDecimal.TEN, BigDecimal.ONE));
    }
}
