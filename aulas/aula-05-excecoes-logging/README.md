# Aula 5 - Tratamento de Exceções e Logging em Java

Disciplina: Introdução à Qualidade de Software com Java Avançado.
Projeto Maven independente (Java 17, JUnit 5.10.2, Log4j2 2.23.1, SLF4J 2.0.13).

## Resumo teórico (em poucas palavras)

- **Hierarquia**: `Throwable` se divide em `Error` (falhas da JVM, não se trata) e `Exception`.
  `Exception` e suas filhas diretas são **checked** (o compilador obriga a tratar ou declarar);
  `RuntimeException` e filhas são **unchecked**.
- **Boas práticas**: criar exceções de negócio com nome expressivo; mensagens claras com o dado que
  explica o erro; não capturar `Exception` genérica; usar *try-with-resources* para fechar
  recursos; **não** fazer *catch-log-throw* (logar e relançar gera o mesmo erro duplicado em
  vários pontos do log). Regra prática: ou se trata, ou se propaga; o log do erro fica na camada
  que de fato o trata.
- **Logging**: Log4j2 é uma implementação rápida; SLF4J é uma fachada que permite trocar a
  implementação. Níveis, do menos ao mais grave: DEBUG, INFO, WARN, ERROR, FATAL. Mensagens
  parametrizadas (`{}`) evitam concatenar strings que talvez nem sejam emitidas.
- **Monitoramento**: Sentry captura erros em tempo real com stack trace; ELK (Elasticsearch,
  Logstash, Kibana) serve para análise histórica de logs.

## Enunciado (entregas da aula)

1. Atividade 1: `SaldoInsuficienteException` e, como exercício, `LimiteSaqueExcedidoException`
   com regra de **no máximo R$ 1.000 por saque**.
2. Atividade 2: configurar Log4j2 (`log4j2.xml` com Console) e, como exercício, **FileAppender**.
3. Atividade 3 (opcional): integrar o Sentry.
4. Desafio: refatorar `CadastroUsuario` com exceção customizada para idade inválida, logging com
   SLF4J e try-with-resources.
5. Discussão: quando uma exceção deve ser checked vs unchecked?
6. Tarefas: configurar Sentry em projeto pessoal; extra: explorar Kibana.

## O que foi feito

| Item | Onde |
|---|---|
| `SaldoInsuficienteException` (unchecked, guarda o saldo) | `src/main/java/br/edu/iqs/aula05/banco/` |
| `LimiteSaqueExcedidoException` + regra de R$ 1.000 em `ContaService.LIMITE_SAQUE` | idem |
| `ContaService` (sacar, depositar, transferir) com Log4j2 | idem |
| `log4j2.xml` com Console **e FileAppender** (`aula05.log`) | `src/main/resources/` |
| Desafio `CadastroUsuario` (`IdadeInvalidaException`, SLF4J, try-with-resources, `ArquivoUsuariosException`) | `src/main/java/br/edu/iqs/aula05/usuario/` |
| Demonstração executável | `Aplicacao.java` |
| Sentry (configuração documentada, **não executado**) | `sentry/log4j2-sentry-exemplo.xml` |
| Testes (25) | `src/test/java/...` |

### Problemas dos slides corrigidos

1. **Catch-log-throw**: o slide de `transferir()` faz `logger.error(...)` e depois `throw e`, que é
   exatamente o anti-padrão que a própria aula manda evitar. Na versão entregue, `transferir` não
   captura nada; a falha é registrada uma única vez em `Aplicacao` (fronteira). O teste
   `LoggingTest.semCatchLogThrow` prova que o serviço não emite nenhum evento ERROR.
2. **`double` para dinheiro**: trocado por `BigDecimal` (sem erros de arredondamento binário).
3. **`CadastroUsuario` do slide não compila**: `fw.close()` no `finally` lança `IOException` não
   tratada, e ainda poderia mascarar a exceção original. Resolvido com try-with-resources.
4. **`System.err.println` para erro de negócio** só imprimia e deixava o cadastro seguir; agora lança
   `IdadeInvalidaException` e o usuário não é gravado.
5. **`e.printStackTrace()`** substituído por exceção encadeada (causa preservada) para o chamador.
6. **Versões**: Log4j2 2.20.0 -> 2.23.1; Sentry 6.25.0 está defasada (usar a versão atual).
   Foi necessário `log4j-slf4j2-impl` (ponte SLF4J 2.x) para o SLF4J usar a mesma configuração
   do Log4j2, algo que o slide não menciona.
7. O slide referencia `saldo` sem declarar e `transferir()` sem parâmetros; foram dados forma e
   assinatura reais (`transferir(destino, valor)`).

Decisões de projeto: o limite é validado **antes** do saldo (regra do produto independe do estado
da conta); limite inclusivo (R$ 1.000,00 exato passa); `ArquivoUsuariosException` é *checked* e
`IdadeInvalidaException` é *unchecked* (justificativa na discussão abaixo).

## Como rodar

```powershell
# Java 17 e Maven no PATH
mvn -B clean test                                        # 25 testes
mvn -q compile exec:java -Dlog.dir=target/logs           # demonstração (gera target/logs/aula05.log)
```

Fora do Maven, o arquivo de log vai para `./logs/aula05.log` (propriedade `log.dir`).
Nos testes ele vai para `target/logs/` (configurado no `pom.xml`).

## Evidências (`evidencias/`)

- `mvn-clean-test.txt`: saída real do `mvn clean test` -> **25 testes, 0 falhas, BUILD SUCCESS**.
- `demo-aplicacao.txt`: saída da demonstração (transferência ok, saldo insuficiente, limite
  excedido, cadastro rejeitado, ERROR com stack trace).
- `aula05-log-fileappender.txt`: conteúdo real do arquivo gerado pelo FileAppender
  (copiado de `target/logs/aula05.log`; o `.gitignore` do repositório ignora `*.log`).

## Sentry (Atividade 3 e tarefa) - NÃO EXECUTADO

Exige criar conta em sentry.io e um DSN, o que está fora do escopo desta entrega. Ficou
**documentado e desativado**:

1. Dependência a acrescentar ao `pom.xml`: `io.sentry:sentry-log4j2` (versão atual).
2. Appender `<Sentry name="Sentry" dsn="${env:SENTRY_DSN:-}" minimumEventLevel="ERROR"/>` no arquivo
   `sentry/log4j2-sentry-exemplo.xml`. O DSN vem só de variável de ambiente (nunca versionado);
   com DSN vazio o SDK não envia nada.
3. O código de teste do slide (`throw new RuntimeException("Erro crítico!")` + `logger.error(...)`)
   está em `Aplicacao.java`; com o appender ativo, esse ERROR viraria um evento no Sentry.

O extra (Kibana/ELK) não foi executado (exige Elasticsearch/Kibana em Docker, indisponível aqui).

## Discussão: quando uma exceção deve ser checked vs unchecked?

- **Checked** quando o chamador *pode e deve* reagir de forma razoável a uma falha *externa e
  esperada*: arquivo inexistente, rede fora, disco cheio. Obrigar a tratar documenta o risco no
  contrato do método. Exemplo aqui: `ArquivoUsuariosException` (o chamador pode escolher outro
  caminho ou avisar o usuário).
- **Unchecked** para violações de regra de negócio ou de contrato/entrada (erro de programação ou
  dado inválido), onde não há recuperação local e a camada superior apenas traduz o erro. Exemplos:
  `SaldoInsuficienteException`, `LimiteSaqueExcedidoException`, `IdadeInvalidaException`.
- Checked em excesso polui assinaturas e leva a `catch` vazios; unchecked em excesso esconde falhas
  recuperáveis. Frameworks modernos (Spring etc.) tendem a preferir unchecked e tratar em um ponto
  central. Em qualquer caso: preservar a causa (`new X(msg, causa)`) e não engolir a exceção.
