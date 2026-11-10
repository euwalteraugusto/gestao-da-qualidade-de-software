package br.gqs.aula03.depois;

import br.gqs.aula03.modelo.Cliente;
import java.io.PrintStream;
import java.util.List;
import java.util.Objects;

/**
 * Relatório de clientes maiores de idade (versão "depois").
 *
 * <p>Correções em relação ao slide: a regra {@code idade > 18} excluía quem tem
 * exatamente 18 anos (maioridade no Brasil é a partir de 18, inclusive); o
 * resultado agora é testável (lista de nomes) e a saída é injetável, em vez de
 * escrever fixo em {@code System.out}.
 */
public class RelatorioClientes {

  /** Idade mínima (inclusive) para ser considerado maior de idade. */
  public static final int IDADE_MAIORIDADE = 18;

  /**
   * Obtém os nomes dos clientes maiores de idade, na ordem da lista.
   *
   * @param clientes lista de clientes; {@code null} é tratado como lista vazia
   * @return nomes dos clientes com idade a partir de {@value #IDADE_MAIORIDADE}
   */
  public List<String> nomesDeMaioresDeIdade(List<Cliente> clientes) {
    if (clientes == null) {
      return List.of();
    }
    return clientes.stream()
        .filter(Objects::nonNull)
        .filter(RelatorioClientes::ehMaiorDeIdade)
        .map(Cliente::getNome)
        .toList();
  }

  /**
   * Imprime, um por linha, os nomes dos clientes maiores de idade na saída padrão.
   *
   * @param clientes lista de clientes; {@code null} não imprime nada
   */
  public void imprimirClientesMaioresDeIdade(List<Cliente> clientes) {
    imprimirClientesMaioresDeIdade(clientes, System.out);
  }

  /**
   * Imprime, um por linha, os nomes dos clientes maiores de idade na saída informada.
   *
   * @param clientes lista de clientes; {@code null} não imprime nada
   * @param saida destino da impressão
   */
  public void imprimirClientesMaioresDeIdade(List<Cliente> clientes, PrintStream saida) {
    nomesDeMaioresDeIdade(clientes).forEach(saida::println);
  }

  private static boolean ehMaiorDeIdade(Cliente cliente) {
    return cliente.getIdade() >= IDADE_MAIORIDADE;
  }
}
