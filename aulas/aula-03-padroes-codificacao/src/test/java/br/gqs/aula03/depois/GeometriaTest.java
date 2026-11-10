package br.gqs.aula03.depois;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class GeometriaTest {

  @Test
  void calculaAreaDoRetangulo() {
    assertEquals(12, Geometria.calcularArea(3, 4));
    assertEquals(0, Geometria.calcularArea(0, 10));
  }

  @Test
  void equivaleAoCalcDaVersaoAntes() {
    assertEquals(new br.gqs.aula03.antes.Geometria().calc(7, 6), Geometria.calcularArea(7, 6));
  }

  @Test
  void rejeitaMedidasNegativas() {
    assertThrows(IllegalArgumentException.class, () -> Geometria.calcularArea(-1, 2));
    assertThrows(IllegalArgumentException.class, () -> Geometria.calcularArea(2, -1));
  }

  @Test
  void overflowLancaArithmeticException() {
    assertThrows(ArithmeticException.class,
        () -> Geometria.calcularArea(Integer.MAX_VALUE, 2));
  }
}
