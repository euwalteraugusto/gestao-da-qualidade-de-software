# Aula 6 - Testes Unitários com JUnit 5

Disciplina: Introdução à Qualidade de Software com Java Avançado.
Projeto Maven independente (Java 17, JUnit Jupiter 5.10.2 via BOM, AssertJ 3.25.3).

## Resumo teórico (em poucas palavras)

- **Por que automatizar testes**: detecção precoce de bugs (antes do deploy), documentação viva
  (o teste descreve o comportamento esperado) e refatoração segura (se os testes seguem verdes, o
  comportamento foi preservado).
- **Anatomia (JUnit 5 / Jupiter)**: `@BeforeAll` (uma vez, método estático) -> `@BeforeEach` -> `@Test`
  -> `@AfterEach` -> ... -> `@AfterAll`. Cada teste roda em uma **nova instância** da classe, então
  testes não compartilham estado por acidente.
- **Anotações**: `@Test`, `@BeforeEach`, `@DisplayName` (nome legível), `@Disabled` (desliga com motivo),
  além de `@ParameterizedTest`, `@Nested`, `@RepeatedTest`, `@Timeout`, `@Tag`.
- **Asserções**: `assertEquals`, `assertTrue/False`, `assertThrows` (exceção esperada), `assertAll`
  (executa todas e reporta todas as falhas de uma vez). AssertJ oferece alternativa fluente.
- Padrão **Arrange-Act-Assert**: preparar, executar, verificar. Um teste deve falhar por um motivo só.

## Enunciado (entregas da aula)

1. Atividade 1: testar `Calculadora` (`somar`, `dividir` -> `DivisaoPorZeroException`). Exercício:
   somas com negativos e limite com `Integer.MAX_VALUE`.
2. Atividade 2: testes parametrizados (`@MethodSource`). Exercício: `dividir` com `(4,2,2)` e `(9,4,2.25)`.
3. Atividade 3: `UsuarioService.validarSenha`. Desafio: `null`, 7 e 8 caracteres, organizados com `@Nested`.
4. Desafio avançado: refatorar o teste de 3 `assertEquals` para `@ParameterizedTest` + `assertAll`.
5. Discussão: quando usar `@ParameterizedTest` vs testes individuais?
6. Tarefa: escrever testes para `StringUtils` (`isEmpty()` e `inverterString()`).

## O que foi feito

| Item do slide | Arquivo |
|---|---|
| Classes sob teste (`Calculadora`, `DivisaoPorZeroException`, `UsuarioService`, `StringUtils`) | `src/main/java/br/edu/iqs/aula06/` |
| Anatomia + Atividade 1 + exercícios (negativos, `MAX_VALUE`), `@Disabled`, `@RepeatedTest`, `@Timeout` | `CalculadoraTest` |
| Atividade 2 + exercício de `dividir` (4,2,2) e (9,4,2.25); `@MethodSource`, `@CsvSource`, `@ValueSource` | `CalculadoraParametrizadaTest` |
| Atividade 3 + desafio (`null`, 7, 8 caracteres, `@Nested`, `@NullAndEmptySource`) | `UsuarioServiceTest` |
| Desafio avançado (`@ParameterizedTest` + `assertAll`) | `DesafioAvancadoTest` |
| Tarefa `StringUtils` | `StringUtilsTest` |
| Ordem do ciclo de vida comprovada por teste | `CicloDeVidaTest` |
| Asserções fluentes (recurso do slide) | `AssertJTest` |

**71 testes executados: 0 falhas, 0 erros, 1 ignorado** (o `@Disabled` é proposital, para demonstrar
a anotação; aparece como *Skipped* no relatório).

### Problemas dos slides corrigidos

1. **Versões misturadas**: o slide adiciona só `junit-jupiter-params` 5.9.0. Usar versões diferentes
   de `api`/`engine`/`params` é fonte de erro. Foi usado o `junit-bom` 5.10.2 e o agregador
   `junit-jupiter`, que traz api, params e engine na mesma versão.
2. **Asserção tautológica no desafio avançado**: a "solução sugerida" faz
   `assertTrue(a + b == esperado)`, que só repete a conta com o `+` do Java e passaria mesmo se
   `Calculadora.somar` estivesse errada. Foi substituída por uma checagem independente: a
   **comutatividade** (`somar(b, a)` também deve dar `esperado`).
3. **Código incompleto dos slides**: `DivisaoPorZeroException` não era definida (criada, estendendo
   `ArithmeticException`); o `@Nested` usava `service` sem declarar (campo + `@BeforeEach` na classe
   externa); faltavam imports (`Stream`, `Arguments`).
4. **Estouro silencioso**: `Calculadora.somar(Integer.MAX_VALUE, 1)` devolve `Integer.MIN_VALUE`
   (volta ao negativo) sem erro. O exercício do `MAX_VALUE` foi resolvido com um *teste de
   caracterização* que documenta esse comportamento e com o método novo `somarExato`
   (`Math.addExact`), que lança `ArithmeticException`.
5. **Comparação de `double`**: `dividir` retorna `double`; os testes usam `assertEquals(esperado,
   obtido, delta)` em vez de igualdade exata.
6. O teste "ruim" do desafio (três asserts em sequência) interrompe no primeiro erro e não diz qual
   caso falhou; a versão parametrizada nomeia cada caso (`somar(1, -2) = -1`).

## Como rodar

```powershell
# Java 17 e Maven no PATH
mvn -B clean test
```

## Evidências (`evidencias/`)

- `mvn-clean-test.txt`: saída real do `mvn clean test` (71 testes, 1 skipped, BUILD SUCCESS).

## Discussão: quando usar `@ParameterizedTest` vs testes individuais?

- **Parametrizado** quando a *mesma lógica de verificação* se aplica a muitos dados de entrada
  (tabelas de casos, valores-limite, classes de equivalência): elimina duplicação, torna fácil
  acrescentar um caso (uma linha) e cada linha vira um teste nomeado no relatório.
- **Individual** quando os cenários têm *preparação ou verificações diferentes* (ex.: exceção vs.
  retorno normal, mensagens distintas), quando o nome do teste precisa contar uma história específica
  ou quando o caso é único. Forçar tudo em uma tabela com `if`s no corpo do teste é um mau cheiro.
- Na prática, deste projeto: `somar` e `dividir` com vários pares viraram parametrizados;
  `divisão por zero` com verificação de mensagem e `somarExato` com estouro específico ficaram
  individuais (ou parametrizados só pelos dados de entrada que compartilham o mesmo `assertThrows`).
