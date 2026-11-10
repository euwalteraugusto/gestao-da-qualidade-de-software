package br.edu.iqs.aula08.carrinho;

/** O cupom informado nao existe. */
public class CupomInvalidoException extends RuntimeException {

    public CupomInvalidoException(String codigo) {
        super("Cupom inválido: " + codigo);
    }
}
