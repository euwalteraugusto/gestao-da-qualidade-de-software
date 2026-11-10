package br.edu.iqs.aula07.produto;

/** Nao ha unidades suficientes em estoque para a reserva pedida. */
public class EstoqueInsuficienteException extends RuntimeException {

    public EstoqueInsuficienteException() {
        super("Estoque insuficiente");
    }

    public EstoqueInsuficienteException(String mensagem) {
        super(mensagem);
    }
}
