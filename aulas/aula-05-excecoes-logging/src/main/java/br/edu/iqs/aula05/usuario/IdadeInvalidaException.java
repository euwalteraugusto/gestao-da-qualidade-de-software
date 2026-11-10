package br.edu.iqs.aula05.usuario;

/**
 * Erro de validacao do cadastro: idade fora do permitido (menor de idade ou valor impossivel).
 * <p>
 * Unchecked: e erro de dado de entrada, detectado antes de qualquer efeito colateral; nao ha o que
 * "recuperar" no ponto da chamada alem de rejeitar o cadastro.
 */
public class IdadeInvalidaException extends RuntimeException {

    private final int idade;

    public IdadeInvalidaException(int idade, String motivo) {
        super("Idade inválida (" + idade + "): " + motivo);
        this.idade = idade;
    }

    public int getIdade() {
        return idade;
    }
}
