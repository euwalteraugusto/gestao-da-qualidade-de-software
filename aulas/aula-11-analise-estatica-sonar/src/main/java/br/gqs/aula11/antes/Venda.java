package br.gqs.aula11.antes;

/** ANTES: dinheiro em double (imprecisão) e classe mutável. */
public class Venda {

    private String cliente;
    private double valor;

    public Venda(String cliente, double valor) {
        this.cliente = cliente;
        this.valor = valor;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }
}
