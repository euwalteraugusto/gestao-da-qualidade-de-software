package br.edu.iqs.aula07.produto;

/** O servico de estoque falhou (erro HTTP inesperado, rede, resposta invalida). */
public class EstoqueIndisponivelException extends RuntimeException {

    public EstoqueIndisponivelException(String mensagem) {
        super(mensagem);
    }

    public EstoqueIndisponivelException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
