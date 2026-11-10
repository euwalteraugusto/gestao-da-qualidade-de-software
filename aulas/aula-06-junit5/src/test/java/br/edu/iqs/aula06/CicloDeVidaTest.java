package br.edu.iqs.aula06;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * Demonstra, de forma verificavel, a ordem do ciclo de vida do JUnit 5:
 * {@code @BeforeAll} (1x) -> {@code @BeforeEach} -> teste -> {@code @AfterEach} -> ... -> {@code @AfterAll}.
 * A ordem dos metodos e fixada com {@code @Order} apenas para tornar o teste deterministico
 * (testes reais nao devem depender de ordem).
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Ciclo de vida do JUnit 5")
class CicloDeVidaTest {

    private static final List<String> eventos = new ArrayList<>();

    @BeforeAll
    static void antesDeTodos() {
        eventos.clear();
        eventos.add("beforeAll");
    }

    @BeforeEach
    void antesDeCada() {
        eventos.add("beforeEach");
    }

    @AfterEach
    void depoisDeCada() {
        eventos.add("afterEach");
    }

    @AfterAll
    static void depoisDeTodos() {
        // Ao final, o afterEach do 2o teste ja foi registrado.
        assertEquals(List.of("beforeAll",
                "beforeEach", "primeiro", "afterEach",
                "beforeEach", "segundo", "afterEach"), eventos);
    }

    @Test
    @Order(1)
    void primeiro() {
        eventos.add("primeiro");
        assertEquals(List.of("beforeAll", "beforeEach", "primeiro"), eventos);
    }

    @Test
    @Order(2)
    void segundo() {
        eventos.add("segundo");
        assertEquals(List.of("beforeAll", "beforeEach", "primeiro", "afterEach", "beforeEach", "segundo"),
                eventos);
    }
}
