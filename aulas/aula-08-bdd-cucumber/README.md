# Aula 8 - Testes de Comportamento (BDD) com Cucumber

Disciplina: Introdução à Qualidade de Software com Java Avançado.
Projeto Maven independente: Java 17, Cucumber 7.20.1 (JUnit Platform), JUnit 5.10.2.

## Resumo teórico (em poucas palavras)

- **BDD (Behavior-Driven Development)** descreve o **comportamento** do sistema em linguagem natural
  compartilhada por devs, QAs e POs, em vez de focar na implementação. A especificação vira teste
  executável e documentação viva.
- **TDD vs BDD**: TDD pergunta "como testar?", trabalha com unidades e linguagem técnica; BDD pergunta
  "o que testar?", trabalha com funcionalidades (features) e linguagem natural. Não são excludentes.
- **Componentes do Cucumber**: (1) arquivos `.feature` com a especificação em **Gherkin**;
  (2) **step definitions**, código Java que implementa cada passo; (3) **test runner**, que localiza
  features e glue e executa tudo.
- **Gherkin em português**: `# language: pt` + `Funcionalidade`, `Contexto` (Background), `Cenário`,
  `Esquema do Cenário` + `Exemplos` (Scenario Outline), `Dado` (preparação), `Quando` (ação),
  `Então` (verificação), `E`/`Mas` (continuação).

## Enunciado (entregas da aula)

1. Atividade 1: configurar Cucumber (dependências, estrutura `steps/`, `runner/`, `features/`).
2. Atividade 2: escrever `calculadora.feature` (soma de dois números; divisão por zero).
3. Atividade 3: implementar `CalculadoraSteps`.
4. Atividade 4: configurar o `TestRunner` (relatórios `pretty` e HTML).
5. Exercício/desafio: feature de **login** (`autenticacao.feature`): login válido e senha inválida.
6. Discussão: quando usar BDD vs testes unitários tradicionais?
7. Tarefa: escrever uma feature para o projeto final em Gherkin com **pelo menos 2 cenários**.

## O que foi feito

```
src/main/java/br/edu/iqs/aula08/
  calculadora/  Calculadora, DivisaoPorZeroException
  autenticacao/ AutenticacaoService (hash PBKDF2, bloqueio após 3 erros), ResultadoLogin
  carrinho/     Carrinho, CupomInvalidoException
src/test/java/
  runner/TestRunner.java          # @Suite do JUnit Platform + engine "cucumber"
  steps/CalculadoraSteps.java     # Atividade 3
  steps/AutenticacaoSteps.java    # Desafio de login
  steps/CarrinhoSteps.java        # Tarefa (feature "do projeto final")
src/test/resources/features/
  calculadora.feature   (soma, divisão por zero, decimal, 2 esquemas de cenário)
  autenticacao.feature  (login válido, senha inválida, usuário inexistente, bloqueio, reset de contagem)
  carrinho.feature      (Contexto + 5 cenários + 1 esquema com tabela de 3 Exemplos)
```

**24 cenários executados (incluindo as linhas dos esquemas), 0 falhas** (`mvn clean test`).
As features incluem exatamente os cenários dos slides (soma 5+7=12, soma 2+3=5, divisão por zero com a
mensagem "Divisão por zero não permitida", login válido, senha inválida) e outros adicionais.

### Problemas dos slides corrigidos

1. **Runner JUnit 4 legado**: o slide usa `cucumber-junit` com `@RunWith(Cucumber.class)` e
   `@CucumberOptions`, que dependem do JUnit 4. O projeto usa JUnit 5, então foi adotado
   `cucumber-junit-platform-engine` + `@Suite`/`@IncludeEngines("cucumber")`/`@SelectClasspathResource`
   com `@ConfigurationParameter` para glue e plugins.
2. **Step de divisão não compilava**: `private int resultado` recebendo `Calculadora.dividir(...)`,
   que devolve `double` (conversão estreitante, erro de compilação). Passou para `double`, e o passo de
   verificação usa `{double}` com tolerância (`assertEquals(esperado, resultado, 1e-9)`).
3. **Decimais em português**: com `# language: pt`, o parâmetro `{double}`/`{bigdecimal}` lê vírgula
   decimal (`2,25`, `R$ 3,50`); foi coberto por cenários reais.
4. **`catch (Exception e)` genérico** nos steps (o próprio slide da Aula 5 pede para evitar):
   trocado por `catch (ArithmeticException e)`.
5. **Imports faltando** nos slides (`assertEquals`, `Dado`, `Quando`, `Então`...): `io.cucumber.java.pt.*`
   e asserções do JUnit 5 (`org.junit.jupiter.api.Assertions`).
6. **Senha em texto**: o desafio de login guarda "1234" em claro no exemplo; aqui o serviço armazena
   hash PBKDF2-HMAC-SHA256 com sal e compara em tempo constante. Continua sendo exemplo didático
   (em produção: bcrypt/Argon2, persistência e limite de tentativas por IP/tempo).
7. **Estado compartilhado**: cada cenário recebe uma instância nova das classes de steps, então o
   estado (`num1`, `exception`) não vaza entre cenários, mas foi documentado nos comentários.
8. **Versão**: Cucumber 7.14.0 -> 7.20.1 (mesma série 7.x, compatível com JUnit Platform 1.10).

## Como rodar

```powershell
# Java 17 e Maven no PATH
mvn -B clean test
# Relatório HTML gerado em: target/cucumber-reports/relatorio.html (e relatorio.json)
```

## Evidências (`evidencias/`)

- `mvn-clean-test.txt`: saída real (formato `pretty` com todos os passos e o método Java associado a
  cada um; `Tests run: 24, Failures: 0`, BUILD SUCCESS). A linha
  `Picked up JAVA_TOOL_OPTIONS: -Dfile.encoding=UTF-8` serve só para o console exibir acentos.
- `cucumber-relatorio.html`: relatório HTML completo do Cucumber (abre no navegador).
- `cucumber-relatorio.png`: captura do relatório (24 PASSED, 3 features).

## Discussão: quando usar BDD vs testes unitários tradicionais?

- **BDD** quando o comportamento precisa ser **entendido e validado por não programadores** (PO, QA,
  negócio), ao especificar funcionalidades de ponta a ponta (login, checkout, regras de desconto), e
  quando se quer documentação executável dos critérios de aceite. Os cenários servem de contrato
  entre as áreas e de teste de regressão.
- **Testes unitários** para a lógica interna e os casos de borda de classes/métodos: rápidos,
  numerosos, escritos e lidos só por desenvolvedores (ex.: overflow de `int`, formatação, validações
  de entrada).
- Custo do BDD: manutenção das frases (Gherkin) e dos steps, e execução mais lenta. Se os cenários
  só descrevem detalhes técnicos ("Dado que o método X é chamado com..."), perdem o sentido; nesse
  caso um teste unitário é melhor. Na prática combinam-se: poucos cenários BDD de comportamento
  relevante, no topo da pirâmide, e muitos testes unitários na base.

## Tarefa: feature para o projeto final

`carrinho.feature` usa um domínio de exemplo (carrinho de compras) com **5 cenários** (mais um esquema
de cenário com 3 exemplos) e `Contexto`; é uma base para adaptar ao tema do projeto final (basta trocar a classe
sob teste e as frases). Não foi vinculada ao projeto final porque ele é desenvolvido em outra pasta.
