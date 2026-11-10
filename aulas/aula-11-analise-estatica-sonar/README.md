# Aula 11 - Análise Estática e Qualidade de Código

Projeto Maven (Java 17) com **código real cheio de code smells** (`br.gqs.aula11.antes`), a versão refatorada guiada por
métricas (`br.gqs.aula11.depois`) e a medição antes/depois feita com **PMD 7 e Checkstyle** como substitutos locais do
SonarQube. A configuração para SonarQube/SonarCloud (projeto, plugin, workflow, quality gate) também está pronta, mas o
servidor Sonar **não foi executado** nesta máquina (sem Docker nem conta): nenhum número abaixo é "do Sonar".

## Entregas (exercícios, desafio e tarefa do PDF)

| Item do PDF | Entrega | Onde |
|-------------|---------|------|
| Atividade 1: SonarQube local via Docker, login, projeto, token | Roteiro + `docker-compose.yml` (não executado) | [`docs/sonarqube-guia.md`](docs/sonarqube-guia.md), `docker-compose.yml` |
| Atividade 2: plugin no `pom.xml`, `mvn sonar:sonar`, explorar dashboard (maior smell, arquivo com mais dívida, método mais complexo) | Plugin 5.8 no `pom.xml`, comando atualizado, onde clicar + previsão por PMD (não executado no servidor) | `pom.xml`, `sonar-project.properties`, guia |
| Atividade 3: refatoração guiada (extrair método, Strategy, testes) de `FinanceUtils` | `antes/` x `depois/` com Strategy, 36 testes de caracterização | `src/main/java/br/gqs/aula11/`, `src/test/java/br/gqs/aula11/` |
| Desafio: reduzir >= 50% a dívida da classe e subir a nota de maintainability | Medido com PMD/Checkstyle: ver tabelas | `evidencias/metricas-antes-depois.md` |
| Tarefa: refatorar **2 classes** guiado por métricas | `FinanceUtils` (Strategy) e `RelatorioVendas` (enum de formatos + streams + BigDecimal) | `antes/`, `depois/` |
| Tarefa: Quality Gate mínimo | Condições documentadas + workflow com `qualitygate.wait` | guia (seção 4), [`aula-11-sonar.yml`](../../.github/workflows/aula-11-sonar.yml) |

## Como reproduzir as medições

```bash
cd aulas/aula-11-analise-estatica-sonar
mvn clean verify                                   # 36 testes + JaCoCo (target/site/jacoco)

# ANTES (alvo = pacote antes). Em PowerShell, coloque os -D entre aspas.
mvn pmd:pmd -Dpmd.alvo="**/antes/**/*.java" -Dpmd.regras=config/pmd-qualidade.xml   # smells  -> target/pmd.xml
mvn pmd:pmd -Dpmd.alvo="**/antes/**/*.java" -Dpmd.regras=config/pmd-metricas.xml    # métricas por método
mvn checkstyle:checkstyle -Dcheckstyle.alvo="**/antes/**/*.java"                    # -> target/checkstyle-result.xml
# DEPOIS: troque "antes" por "depois" (é o padrão do pom, então os -D podem ser omitidos)
mvn pmd:check checkstyle:check                     # GATE no código refatorado: 0 violações

powershell -File scripts/resumo-metricas.ps1       # consolida os XML (copiados para evidencias/) em tabelas
```

## Resultados reais (PMD 7.17.0, Checkstyle 10.17.0; JDK 17.0.21, Maven 3.9.9)

### Code smells encontrados no "antes" (antes/FinanceUtils e antes/RelatorioVendas)

| Smell | Regra PMD (equivalente Sonar) | Ocorrências |
|-------|-------------------------------|------------:|
| `tipo.equals("ICMS")` com `tipo` possivelmente nulo (**bug: NPE**, o teste `tipoNuloEraBugNaVersaoAntes...` prova) | `LiteralsFirstInComparisons` (S1132) | 11 |
| Concatenação de String em laço | `UseStringBufferForStringAppends` (S1643) | 6 |
| Literais duplicados (`"CSV"`, `"TXT"`...) | `AvoidDuplicateLiterals` (S1192) | 5 |
| Variáveis locais sem uso | `UnusedLocalVariable` (S1481) | 2 |
| Complexidade ciclomática / cognitiva altas | `CyclomaticComplexity`, `CognitiveComplexity` (S3776) | 2 + 2 |
| ifs aninhados em 4 níveis | `AvoidDeeplyNestedIfStmts` | 1 |
| Laço com índice em vez de for-each | `ForLoopCanBeForeach` | 1 |
| `if` sem chaves | `ControlStatementBraces` (S121) | 1 |
| Campo privado sem uso | `UnusedPrivateField` (S1068) | 1 |
| `System.out` em regra de negócio | `SystemPrintln` (S106) | 1 |
| Classe só com dados (dinheiro em `double`, mutável) | `DataClass` | 1 |
| **Total PMD** | | **34** (FinanceUtils 13, RelatorioVendas 20, Venda 1) |

Checkstyle (outro medidor): 16 violações no "antes" (FinanceUtils 6, RelatorioVendas 10): `MultipleStringLiterals` 6, `MethodLength` 2,
`JavaNCSS` 2, `CyclomaticComplexity` 2, e 1 de cada `NestedIfDepth`, `NPathComplexity`, `VisibilityModifier` (campo público), `NeedBraces`.

### Comparativo (de `evidencias/metricas-antes-depois.md`)

| Métrica | Antes | Depois | Variação |
|---------|------:|-------:|---------:|
| Violações PMD (smells) | 34 | 0 | -100% |
| Violações Checkstyle | 16 | 0 | -100% |
| Ciclomática de `FinanceUtils.calcularImposto` | 9 | 4 | -56% |
| Ciclomática de `RelatorioVendas.gerar` | 10 | 1 | -90% |
| Maior ciclomática de método (pacote inteiro) | 10 | 5 | -50% |
| Maior complexidade cognitiva de método | 16 | 3 | -81% |
| Maior NPath de método | 28 | 6 | -79% |
| Maior NCSS (linhas de comando) de método | 31 | 7 | -77% |
| Linhas de código (ncloc, aprox.) | 83 | 131 | +58% (mais classes, cada uma simples) |
| Dívida estimada* | 170 min | 0 min | -100% |
| Dívida / (ncloc x 30 min)* | 6,8% | 0% | |
| Nota de manutenibilidade estimada* | **B** | **A** | sobe 1 nível |

\* **Estimativa nossa, não é saída do Sonar**: 5 min por violação PMD (aproximação do *remediation time*) e custo de
desenvolvimento de 30 min por linha (padrão do SonarQube), com faixas A<=5%, B<=10%, C<=20%, D<=50%, E. O critério do
desafio ("redução >= 50% na dívida da classe e subir a nota") é atendido nesta estimativa: por classe, `FinanceUtils`
foi de 13 violações PMD para 0 e `RelatorioVendas` de 20 para 0. A confirmação oficial depende de rodar o Sonar (guia).

### Refatoração 1: `FinanceUtils` (Strategy Pattern)

Antes: cadeia `if / else if` com 5 impostos (ciclomática 9, cognitiva 16, NCSS 31). Depois (`depois/`):
`ImpostoStrategy` + `AliquotaFixaStrategy` (ICMS, ISS, PIS) + `AliquotaPorFaixaStrategy` (IPI, COFINS), registradas em um `Map`;
`calcularImposto` ficou com 2 decisões. Equivalência provada por **testes de caracterização** (`FinanceUtilsTest`: os mesmos 10 casos rodam nas duas versões).

Correções em relação à "solução parcial" do slide:

| Slide | Problema | Aqui |
|-------|----------|------|
| `!estrategias.containsKey(tipo)` com `Map.of(...)` | `Map.of().containsKey(null)` lança `NullPointerException`: a "solução" **troca um NPE por outro** quando `tipo == null` | `tipo == null` tratado antes de consultar o mapa (teste `tipoNuloEraBugNaVersaoAntesEFoiCorrigido`) |
| Comentário "Complexidade agora é 1" | Medido pelo PMD: 4 (`||` e o ternário contam) | Valor real documentado acima |
| Solução parcial só mostra ICMS e IPI | Faltam as demais estratégias ("mais 4-5 condições") | ICMS, IPI, ISS, PIS, COFINS completas |
| IPI "valor > 10000 -> 10%" | Limite exclusivo (10000 -> 5%) | Preservado e testado (`IPI 10000` = 500,00) |

### Refatoração 2: `RelatorioVendas`

Antes: 4 níveis de `if`, `saida = saida + ...` em laço, `double` para dinheiro, formato repetido em dois blocos.
Depois: enum `FormatoRelatorio` (cada formato sabe formatar linha e rodapé), `Venda` como `record` com `BigDecimal`,
`StringBuilder` e streams. Saídas idênticas para CSV/TXT/HTML (teste `duasVersoesGeramAMesmaSaida`).

Mudanças de comportamento **intencionais e documentadas**: formato desconhecido/nulo lança `IllegalArgumentException`
(antes: texto vazio sem aviso / NPE); `tipo` nulo em impostos devolve zero (antes: NPE); dinheiro agora em `BigDecimal`
(a impressão de `50` é `50`, não `50.0`; por isso a comparação usa valores com uma casa decimal); lista vazia imprime `TOTAL;0`.

## Correções em relação aos slides

| Slide | Problema | Correção |
|-------|----------|----------|
| `sonar-maven-plugin` 3.9.1.2184 | Versão de 2022 | 5.8.0.7211 |
| `-Dsonar.login=SEU_TOKEN` | Parâmetro removido nas versões atuais | `-Dsonar.token` (via variável de ambiente) |
| `# comentário` no meio do comando com `\` | Quebra a linha de continuação do shell | Comentários fora do comando |
| Quality gate: "dívida < 1 dia" (aula 11) x "< 1h" (aula 10) | Inconsistência entre aulas | Escolha registrada no guia (1h no código novo) |
| `FinanceUtils` refatorado do slide | `containsKey(null)` com `Map.of` lança NPE | Corrigido (ver acima) |
| `docker run ... sonarqube:community` + `admin/admin` | Sem persistência; o Sonar exige trocar a senha no 1º login | `docker-compose.yml` com PostgreSQL e volumes; guia cita a troca |
| Workflow do Sonar sem cobertura | Gate de cobertura sempre em 0% | JaCoCo XML gerado antes do scan |

## Não executado / limitações

- **SonarQube/SonarCloud**: não executados (sem Docker, servidor ou conta). `mvn sonar:sonar`, dashboard, rating e dívida oficiais ficam pendentes; use o guia. `docker-compose.yml` e `aula-11-sonar.yml` não foram executados (YAML validado só quanto à sintaxe).
- Dívida técnica e nota A-E aqui são **estimativas** a partir de PMD/Checkstyle com as premissas declaradas.
- O workflow `aula-11-sonar.yml` precisa dos secrets `SONAR_TOKEN` e `SONAR_HOST_URL` (e da variável `SONAR_ORGANIZATION` no SonarCloud); sem eles o job do Sonar é pulado com aviso. O job `analise-local` (PMD/Checkstyle/testes) roda sempre.
- Para o orquestrador: nenhuma ação obrigatória; opcionalmente criar os secrets e rodar o Sonar para substituir as estimativas por números oficiais.
