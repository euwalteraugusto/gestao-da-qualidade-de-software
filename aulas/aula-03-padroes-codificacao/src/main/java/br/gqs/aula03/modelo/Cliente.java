package br.gqs.aula03.modelo;

/** Cliente com nome e idade (classe imutável usada pelos relatórios). */
public final class Cliente {

  private final String nome;
  private final int idade;

  /**
   * Cria um cliente.
   *
   * @param nome nome do cliente, não pode ser nulo nem vazio
   * @param idade idade em anos, não pode ser negativa
   * @throws IllegalArgumentException se algum argumento for inválido
   */
  public Cliente(String nome, int idade) {
    if (nome == null || nome.isBlank()) {
      throw new IllegalArgumentException("Nome é obrigatório.");
    }
    if (idade < 0) {
      throw new IllegalArgumentException("Idade não pode ser negativa.");
    }
    this.nome = nome;
    this.idade = idade;
  }

  /**
   * Retorna o nome do cliente.
   *
   * @return nome do cliente
   */
  public String getNome() {
    return nome;
  }

  /**
   * Retorna a idade do cliente.
   *
   * @return idade em anos
   */
  public int getIdade() {
    return idade;
  }
}
