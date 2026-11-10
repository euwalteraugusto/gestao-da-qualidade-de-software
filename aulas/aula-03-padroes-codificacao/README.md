# Aula 3 - Padrões de Codificação e Boas Práticas em Java

## 1. Resumo teórico

- **Por que padronizar:** consistência (código uniforme na equipe), legibilidade (manutenção e *onboarding* mais fáceis) e prevenção de bugs (más práticas detectadas cedo).
- **Ferramentas de análise estática** (analisam o código sem executá-lo):

| Ferramenta | Função | Exemplo de regra |
|---|---|---|
| Checkstyle | verifica **estilo** de código | indentação, espaços, Javadoc, tamanho de linha |
| PMD | detecta **más práticas** e complexidade | evitar `if` aninhados, variáveis não usadas |
| SonarQube | análise **completa** de qualidade (servidor, histórico, *quality gate*) | cobertura mínima de testes |

- **Boas práticas em Java:** classes em `PascalCase`, métodos/variáveis em `camelCase`, constantes em `UPPER_CASE`; uma responsabilidade por classe/método; evitar métodos com mais de 3 parâmetros.

## 2. Enunciado

1. **Atividade 1:** configurar o Checkstyle (`maven-checkstyle-plugin`, `google_checks.xml`), rodar `mvn checkstyle:check` e corrigir os erros (ex.: `calc(int x,int y)` vira `calcularArea(int largura, int altura)`).
2. **Atividade 2:** instalar o PMD no projeto e refatorar `Relatorio.gerar` (nomes descritivos, extração de métodos, remoção de `if` aninhado). Solução sugerida do slide: *early return* + Stream API.
3. **Desafio (25 min):** refatorar `Pagamento.processar(String t, double v)` aplicando Checkstyle (nomenclatura), PMD (complexidade) e SOLID, usando **Strategy** para tipos de pagamento, métodos curtos e validações.
4. **Discussão:** qual ferramenta foi mais útil e por quê? **Tarefa:** configurar SonarQube local (docker-compose).

## 3. O que foi feito

Projeto Maven (Java 17, JUnit 5.10.2, Checkstyle 10.17.0 via `maven-checkstyle-plugin` 3.6.0, PMD 7.7.0 via `maven-pmd-plugin` 3.26.0).

```
src/main/java/br/gqs/aula03/
  modelo/Cliente.java                 classe imutável usada pelos relatórios
  antes/   Relatorio, Geometria, Pagamento      versões RUINS (mantidas de propósito)
  depois/  RelatorioClientes, Geometria         versões limpas (Atividades 1 e 2)
           PagamentoStrategy, PagamentoCredito, PagamentoBoleto,
           PagamentoService, ResultadoPagamento (Desafio: Strategy + SOLID)
config/pmd-ruleset.xml                regras do PMD escolhidas para a aula
sonarqube/docker-compose.yml          SonarQube local (NÃO executado, ver seção 7)
scripts/relatorio-antes-depois.ps1    reproduz o relatório comparativo
```

### Atividade 1: Checkstyle
- `pom.xml` configura `google_checks.xml` com a execução `checkstyle-gate` na fase `verify` (falha o build se houver violação de severidade `warning` ou maior).
- `antes/Geometria.calc(int x,int y){...}` gera violações; `depois/Geometria.calcularArea(int largura, int altura)` as corrige e ainda valida entradas e overflow (`Math.multiplyExact`).

### Atividade 2: PMD
- `config/pmd-ruleset.xml` ativa regras de aninhamento (`AvoidDeeplyNestedIfStmts` com profundidade 2, `CollapsibleIfStatements`), complexidade (ciclomática ≥ 7, cognitiva ≥ 8), parâmetros demais (≥ 4), nomes curtos (`ShortVariable`), `SystemPrintln`, `LiteralsFirstInComparisons` etc.
- `Relatorio.gerar(List<Cliente> c)` virou `RelatorioClientes` com *early return* para lista nula, Stream API (`filter`/`map`) e saída injetável.

### Desafio: `Pagamento` refatorado
`Pagamento.processar(String t, double v)` (if/else por tipo, método longo, parâmetros sem nome, aninhamento) virou:

- `PagamentoStrategy` (interface) com `PagamentoCredito` (taxa 2,99% e parcelas conforme o total) e `PagamentoBoleto` (tarifa R$ 3,50, vencimento em 3 dias);
- `PagamentoService`: valida a entrada e delega à estratégia do tipo (mapa de estratégias injetado). **Novo tipo = nova classe**, sem editar as existentes (OCP; coberto por teste com um tipo `PIX` criado só no teste);
- `ResultadoPagamento` (record imutável) em vez de `System.out` dentro da regra de negócio.

As regras de negócio dos slides eram só comentários ("20 linhas de código", "15 linhas"); as regras concretas (taxa de 2,99%, parcelas, tarifa de boleto) são **inventadas por mim** para ter comportamento testável, e a versão "antes" implementa as mesmas regras.

### Correções e decisões em relação aos slides

| Slide | Problema | Decisão |
|---|---|---|
| `maven-checkstyle-plugin` 3.2.1 | Versão antiga (Checkstyle 9.3) | Plugin 3.6.0 + Checkstyle 10.17.0 (suporta `record`, Java 17). |
| "Máx. 80 caracteres por linha" (tabela) | O `google_checks.xml` usado no exemplo limita a **100** colunas e exige indentação de **2** espaços. | Mantive o Google Style (a configuração do passo a passo); o código "depois" segue 2 espaços e 100 colunas. |
| `if(cli.getIdade() > 18)` | "Maior de idade" a partir de 18 inclusive; `> 18` exclui quem tem exatamente 18 anos (bug de regra). | `>= 18` (constante `IDADE_MAIORIDADE`), com teste na fronteira; o teste "antes" documenta a falha. |
| `forEach(... System.out.println ...)` | PMD `SystemPrintln` e método não testável. | Método devolve a lista de nomes; impressão recebe um `PrintStream`. |
| `t.equals("CREDITO")` | `NullPointerException` se `t` for nulo; tipo desconhecido é ignorado em silêncio. | `IllegalArgumentException` com mensagem clara para nulo/desconhecido. |
| `Pagamento` sem validação de `v` | Aceitava valores negativos/zero/NaN | Validação no `PagamentoService`. |
| Classe `Cliente` | Não existe nos slides | Criada (imutável, validada). |

## 4. Como rodar

```bash
cd aulas/aula-03-padroes-codificacao
mvn -B clean verify        # testes + Checkstyle + PMD sobre o código "depois" (gate: deve passar)
mvn checkstyle:check       # só o Checkstyle (comando do slide)

# relatório do código "antes" (não falha o build):
mvn -B verify -DskipTests "-Dquality.includes=**/antes/**" "-Dquality.fail=false"
```
(Em PowerShell, manter as aspas nos `-D`.) O script `scripts/relatorio-antes-depois.ps1` repete tudo e copia os XML para `evidencias/`.

## 5. Resultados e evidências

**Testes:** 42 testes, 0 falhas (`evidencias/depois-mvn-clean-verify.txt`). Os testes cobrem a versão "antes" (caracterização, inclusive falhas conhecidas), a "depois" e a **equivalência** entre elas (por exemplo, o comprovante do `PagamentoService` é idêntico ao texto que o `Pagamento` antigo imprime).

### Relatório antes x depois

| Análise | ANTES (`antes/`) | DEPOIS (`modelo/` + `depois/`) |
|---|---|---|
| Checkstyle (`google_checks`) | **52 violações** | **0** |
| PMD (ruleset da aula) | **14 violações** | **0** |
| Build `mvn verify` | falharia (rodado com `quality.fail=false`) | `BUILD SUCCESS` |

Detalhe das violações do "antes" (`evidencias/resumo-antes-depois.txt`):

- Checkstyle: 35 `IndentationCheck` (4 espaços em vez de 2), 8 `WhitespaceAroundCheck`, 6 `WhitespaceAfterCheck` (`if(`, `x,int y`), 2 `MissingJavadocTypeCheck`, 1 `MissingJavadocMethodCheck`.
- PMD: 6 `ShortVariable` (`x`, `y`, `t`, `v`, `p`, `c`), 3 `SystemPrintln`, 2 `LiteralsFirstInComparisons`, 1 `AvoidDeeplyNestedIfStmts` (`Relatorio.gerar`), 1 `CyclomaticComplexity` (9) e 1 `CognitiveComplexity` (9) em `Pagamento.processar`.

Arquivos em `evidencias/`: `antes-checkstyle-result.xml`, `antes-pmd.xml`, `antes-mvn-verify.txt` (saída do console com cada violação), `depois-checkstyle-result.xml`, `depois-pmd.xml`, `depois-mvn-clean-verify.txt`, `resumo-antes-depois.txt` e os logs `*-relatorios-mvn.txt`.

Observação: o relatório HTML do Checkstyle/PMD (`target/site`) não foi capturado em imagem; os XML e os logs de console são a evidência.

## 6. Discussão: qual ferramenta foi mais útil? Por quê?

Depende do problema:

- **Checkstyle** deu o maior volume de achados (52), mas quase todos mecânicos (indentação, espaços) e corrigíveis por formatador. Útil para padronizar a equipe e eliminar discussões de estilo em *code review*.
- **PMD** achou os problemas de **design**: aninhamento, complexidade alta e nomes curtos. Para este exercício foi o mais útil, porque apontou o que de fato dificulta a manutenção do `Pagamento` (complexidade 9) e levou ao Strategy.
- **Ressalva:** nenhuma das duas achou o bug de regra `> 18` nem o `NullPointerException` em `t.equals(...)`; quem achou foram os **testes** nas bordas (18 anos, tipo nulo). Análise estática e testes se complementam.
- **SonarQube** reúne tudo isso com histórico e *quality gate*, mas exige servidor (item 7).

## 7. Itens não executados

- **SonarQube local (docker-compose): NÃO executado.** A máquina não tem Docker. Deixei pronto o `sonarqube/docker-compose.yml` (SonarQube 10.6 Community + PostgreSQL) e o comando `mvn sonar:sonar` documentado no cabeçalho do arquivo; nunca foi subido nem testado. As senhas do compose são valores locais de exemplo, e o token do Sonar deve ser criado por quem for rodar (não há segredos no repositório).
- Relatório HTML do Checkstyle/PMD em imagem: não capturado (ver seção 5).
