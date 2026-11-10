package br.gqs.aula03.depois;

/** Cálculos geométricos simples (versão "depois": nomes descritivos). */
public final class Geometria {

  private Geometria() {
  }

  /**
   * Calcula a área de um retângulo.
   *
   * @param largura largura, não negativa
   * @param altura altura, não negativa
   * @return área ({@code largura * altura})
   * @throws IllegalArgumentException se alguma medida for negativa
   * @throws ArithmeticException se a área estourar o limite de {@code int}
   */
  public static int calcularArea(int largura, int altura) {
    if (largura < 0 || altura < 0) {
      throw new IllegalArgumentException("Largura e altura não podem ser negativas.");
    }
    return Math.multiplyExact(largura, altura);
  }
}
