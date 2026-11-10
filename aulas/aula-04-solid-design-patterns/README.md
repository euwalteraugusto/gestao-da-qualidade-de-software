# Aula 4 - Java e POO Avançada: SOLID, Design Patterns e Boas Práticas

## 1. Resumo teórico

**SOLID**

| Princípio | Ideia | Violação -> correção (exemplo) |
|---|---|---|
| **S**RP | uma classe, uma responsabilidade | `Usuario` que também salva no banco -> separar `Usuario` e `UsuarioRepository` |
| **O**CP | aberto para extensão, fechado para modificação | `if/else` para cada novo tipo -> interfaces/herança (Strategy) |
| **L**SP | subclasses devem poder substituir a base | `Pinguim` herdando `Ave.voar()` -> interface `Voador` separada |
| **I**SP | várias interfaces específicas > uma geral | interface com 20 métodos -> `Logavel`, `Salvavel` |
| **D**IP | depender de abstrações | `new MySqlConnection()` -> depender de `DatabaseConnection` (interface) |

**Design Patterns:** *Singleton* (uma única instância, ex.: conexão com BD), *Factory* (centraliza a criação de objetos) e *Strategy* (algoritmos intercambiáveis, ex.: crédito/boleto).

**Boas práticas de POO:** preferir composição a herança; encapsulamento (atributos `private`); evitar acoplamento alto (`new` dentro de classes -> injeção de dependência).

## 2. Enunciado

1. **Atividade 1 (SOLID):** refatorar `Relatorio` (`gerarRelatorio(String tipo)` com `if/else` + `salvarNoBanco()`) aplicando **SRP** (separar geração e persistência) e **OCP** (Strategy para novos formatos).
2. **Atividade 2:** Singleton `DatabaseConnection` e Factory `DocumentoFactory` (PDF/DOCX). Exercício: implementar um **`Logger` como Singleton** e uma **`PaymentFactory`** para `CreditCard` e `Pix`.
3. **Desafio (30 min):** refatorar `CalculadoraImposto` (`if/else` ICMS/ISS) com **Strategy** e **DIP**.
4. **Discussão:** qual princípio SOLID é mais desafiador? **Tarefa:** refatorar um projeto pessoal aplicando Factory Method.

## 3. O que foi feito (mapeamento enunciado -> código)

Projeto Maven (Java 17, JUnit 5.10.2, Mockito 5.11.0, JaCoCo). Pacote base `br.gqs.aula04`; nomes de classes dos slides mantidos (em português) e os do exercício de pagamento em inglês, como no enunciado.

| Item | Antes (ruim, mantido) | Depois |
|---|---|---|
| Atividade 1 (SRP + OCP + DIP) | `antes/RelatorioAntes` (if/else + persistência juntos) | `relatorio/`: `FormatoRelatorio` (Strategy), `FormatoPdf`, `FormatoHtml`, `RelatorioRepository` (abstração) + `RelatorioRepositoryEmMemoria`, `RelatorioService` (recebe o repositório por construtor), `DadosRelatorio` |
| Singleton | n/a | `singleton/DatabaseConnection` (versão do slide, `synchronized` + lazy) e `singleton/Logger` (exercício; idiom *holder*, thread-safe sem `synchronized`) |
| Factory | n/a | `factory/DocumentoFactory` (`Pdf`, `Docx`), `factory/PaymentFactory` (`CreditCard`, `Pix`) e, para a tarefa opcional, `GeradorDocumento` (Factory Method clássico por herança) |
| Desafio (Strategy + DIP) | `antes/CalculadoraImpostoAntes` | `imposto/`: `ImpostoStrategy`, `Icms`, `Iss`, `CalculadoraImposto`, `NotaFiscalService` (depende da abstração, injetada no construtor) |
| LSP e ISP (completam o SOLID) | n/a | `solid/`: `Ave`/`Voador`/`Pardal`/`Pinguim` (LSP), `Logavel`/`Salvavel`/`Usuario` (ISP), `UsuarioRepository` (SRP) |

### Correções e decisões em relação aos slides

- **Slide do `Relatorio`:** `RelatorioService` tinha um `repository` que nunca era atribuído (campo `null`, sem construtor). Corrigido com injeção por construtor (`Objects.requireNonNull`) e o `RelatorioRepository` ganhou uma implementação real em memória.
- **`gerar()` sem parâmetros:** a interface do slide não recebia dados; aqui recebe `DadosRelatorio` e **retorna** o texto (testável). PDF é simulado em texto (sem biblioteca de PDF) e o HTML escapa `& < >` (a versão "antes" não escapava: risco de injeção de HTML).
- **`DocumentoFactory`:** o `switch` do slide lançava `NullPointerException` para `null` e era sensível a maiúsculas; agora lança `IllegalArgumentException` e normaliza o tipo. As classes `PDF`/`Docx` foram nomeadas `Pdf`/`Docx` (convenção de siglas) e implementam uma interface `Documento` (o slide a usa mas não a define).
- **"Factory Method" do slide** é, na verdade, uma *fábrica estática simples* (Simple Factory). Para a tarefa opcional implementei também o Factory Method do GoF (`GeradorDocumento`), que escolhe o produto por polimorfismo.
- **`CalculadoraImposto` do slide** devolvia `0` em silêncio para tipo desconhecido (esconde erro): na versão nova, tipo desconhecido nem existe (recebe-se uma estratégia) e valor negativo/NaN lança `IllegalArgumentException`. Alíquotas de ICMS (10%) e ISS (5%) são as didáticas do slide, não as reais.
- **Singleton do slide:** `synchronized` em todo `getInstance()` é correto, mas bloqueia a cada chamada; o `Logger` usa o *holder idiom* como alternativa e ambos têm teste de 200 threads concorrentes. Limitação conhecida: Singleton clássico pode ser quebrado por reflexão/serialização e dificulta testes (estado global); por isso o `Logger` tem `limpar()` e o *Singleton* aqui não é injetado nas outras classes (usar DIP quando possível).

## 4. Como rodar

```bash
cd aulas/aula-04-solid-design-patterns
mvn -B clean test
# cobertura: target/site/jacoco/index.html
```

## 5. Resultados e evidências

- **54 testes, 0 falhas, 0 erros, BUILD SUCCESS** (`evidencias/mvn-clean-test.txt`): `RelatorioTest` 11, `FactoryTest` 16, `ImpostoTest` 12, `SingletonTest` 10, `SolidTest` 5.
- Os testes cobrem **as duas versões**: caracterização do "antes" (inclusive as violações de OCP/SRP: `gerarRelatorio("CSV")` retorna `null`, geração e persistência juntas) e do "depois", com **equivalência** entre elas (mesma saída de PDF/HTML e mesmos impostos de ICMS/ISS).
- **OCP demonstrado em teste:** um formato CSV e um imposto IPI são criados *dentro do teste* como lambda, sem editar nenhuma classe de produção.
- **DIP demonstrado em teste:** `RelatorioService` usado com um `mock` do Mockito do `RelatorioRepository` (verifica que `salvar("Vendas", relatório)` foi chamado, e que `gerar` não persiste) e `NotaFiscalService` usado com um imposto falso.
- **Singleton:** mesma instância, construtores privados, 200 threads obtendo a instância (1 única), 50 threads escrevendo no `Logger` sem perder mensagens.
- **Cobertura JaCoCo** (`evidencias/jacoco.csv`, `evidencias/jacoco-relatorio.jpg`): 100% das instruções (661/661) e 98% dos ramos (53/54; o ramo faltante é a condição `isInfinite` da validação em `CalculadoraImposto`, que só os valores `NaN` e negativo exercitam).
- Observação: a saída do Maven mostra um aviso `OpenJDK ... Sharing is only supported for boot loader classes` — é um aviso do JDK quando o Mockito/JaCoCo carregam agentes; não afeta o resultado.

## 6. Discussão: qual princípio SOLID é mais desafiador?

Na minha experiência neste exercício, o mais desafiador é o **DIP** (junto com o **LSP**, mais sutil):

- O **DIP** exige decidir **onde** as dependências são criadas (quem faz o `new`). Basta um `new` escondido para a classe voltar a ficar acoplada e difícil de testar; o ganho só aparece nos testes (trocar o repositório por um *mock*) e na troca de implementações. Também pede cuidado para não criar abstrações demais "por via das dúvidas".
- O **LSP** é difícil porque a violação não aparece na compilação (por exemplo, `Pinguim.voar()` lançando exceção compilaria). A correção está no *design* (separar `Voador`), e o conceito exige pensar em contratos, não só em tipos.
- **SRP** e **OCP** são mais fáceis de reconhecer por sintomas (`if/else` por tipo, classe que faz tudo), embora decidir o tamanho de uma "responsabilidade" seja subjetivo.
- **ISP** é o mais direto de aplicar.

## 7. Tarefa opcional: refatorar um projeto pessoal com Factory Method

Não tenho um projeto pessoal disponível neste ambiente para refatorar, então **não executei a tarefa como pedida**. Como substituto, implementei o **Factory Method clássico** (`factory/GeradorDocumento` com subclasses `GeradorPdf` e `GeradorDocx`) e o testei em `FactoryTest`, para praticar o padrão.

## 8. Itens não executados

- Refatoração de projeto pessoal (seção 7): substituída pelo exemplo `GeradorDocumento`.
- Não há banco de dados real: `DatabaseConnection` e o repositório são simulados em memória; o PDF é simulado como texto.
