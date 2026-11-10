# Aula 2 - Metodologias Ágeis e Qualidade (Scrum + CI/CD) com Prática em Java

## 1. Resumo teórico

**Scrum** é um framework ágil iterativo.
- Papéis: *Product Owner* (dono do valor/backlog), *Scrum Master* (remove impedimentos, cuida do processo) e *Dev Team* (constrói o incremento).
- Artefatos: *Product Backlog*, *Sprint Backlog* e *Incremento*.
- Eventos: *Sprint Planning*, *Daily*, *Sprint Review* e *Retrospective*.

**Kanban** visualiza o fluxo em um quadro (A Fazer, Fazendo, Feito) e limita o trabalho em progresso (**WIP**), o que evita sobrecarga e expõe gargalos.

**Como isso melhora a qualidade:** feedback constante (revisões e retrospectivas frequentes), entrega incremental (bugs detectados cedo, enquanto o código ainda está fresco) e transparência (todos veem o progresso e os problemas).

**CI (Integração Contínua):** a cada commit o build e os testes rodam automaticamente. **CD (Entrega/Implantação Contínua):** automatiza o deploy em ambientes de teste/produção. Ferramentas: GitHub Actions, Jenkins, GitLab CI. Benefícios: reduz o *integration hell* (integrar tudo só no fim) e garante que o código na branch principal sempre passa nos testes.

## 2. Enunciado

1. **Passo 1:** criar um projeto Java simples (Maven/Gradle) com um teste JUnit (`Calculator.add` + `CalculatorTest.testAdd`).
2. **Passo 2:** configurar GitHub Actions (`.github/workflows/ci.yml`: checkout, JDK 17 Temurin, `mvn clean test`), fazer commit/push e verificar a execução na aba *Actions*.
3. **Desafio:** adicionar o método `subtract()` em `Calculator`, um teste JUnit para ele, fazer push e verificar se o CI passa.
4. **Discussão:** "Como CI ajuda a manter a qualidade?" **Opcional:** pesquisar "GitHub Actions para deploy automático".

## 3. O que foi feito

- Projeto Maven (`pom.xml`, Java 17, JUnit 5.10.2) com `Calculator` (`add` e `subtract`) e `CalculatorTest` (`testAdd`, `testSubtract` do slide mais casos de borda e overflow). Total de 14 testes.
- Workflow de CI do repositório: [`.github/workflows/aula-02-ci.yml`](../../.github/workflows/aula-02-ci.yml).

### Adaptações e correções em relação aos slides

| Slide | Problema | Correção aplicada |
|---|---|---|
| `Calculator.add` com Javadoc `@throws ArithmeticException Se ocorrer overflow` | O código `a + b` **nunca** lança exceção; o overflow é silencioso (`MAX_VALUE + 1` vira negativo). Javadoc e código discordavam. | Uso de `Math.addExact` / `Math.subtractExact` e testes de overflow. |
| `actions/checkout@v2` e `actions/setup-java@v2` | Versões antigas (rodam em Node antigo, descontinuado). | `actions/checkout@v4` e `actions/setup-java@v4`. |
| `mvn clean test` | Sem modo *batch*; saída poluída com progresso de download. | `mvn -B clean test`. |
| Workflow em `.github/workflows/ci.yml` rodando em todo push | Este repositório tem **uma pasta/projeto Maven por aula**; um `ci.yml` único na raiz não encontraria o `pom.xml`. | Workflow específico da aula, com `paths: aulas/aula-02-*/**` e `defaults.run.working-directory` apontando para a pasta da aula. |
| Classes sem pacote | Pacote padrão (default) é má prática e dificulta o uso no Maven/Checkstyle. | Pacote `br.gqs.aula02`; `Calculator` é `final` com construtor privado. |

Extras no workflow: cache do Maven (`cache: maven`), disparo manual (`workflow_dispatch`) e publicação dos relatórios do Surefire como artefato (`upload-artifact@v4`).

### Explicação do workflow

```yaml
on: push/pull_request com paths: aulas/aula-02-*/**   # só roda se a aula 2 mudar
defaults.run.working-directory: aulas/aula-02-metodologias-ageis-ci
steps: checkout@v4 -> setup-java@v4 (temurin 17) -> mvn -B clean test -> upload dos relatórios
```

## 4. Como rodar

```bash
cd aulas/aula-02-metodologias-ageis-ci
mvn -B clean test
```

## 5. Resultados e evidências

Execução local real (`evidencias/mvn-clean-test.txt`): **14 testes, 0 falhas, 0 erros, BUILD SUCCESS**.

### Item não executado: o pipeline no GitHub

O passo "git push e verificar a aba *Actions*" depende do repositório remoto e **não pôde ser executado por mim** neste ambiente (não faço commit/push; o envio é feito depois pelo aluno/orquestrador). Portanto:

- **Não há evidência (print) de execução do workflow no GitHub nesta pasta.** Ela deve ser adicionada depois do primeiro push, com a captura da aba *Actions* ou o link da execução.
- O arquivo YAML foi escrito com cuidado, mas **não foi validado por um parser nem executado**; o equivalente local do que o job faz (`mvn -B clean test` dentro da pasta da aula) foi executado com sucesso.

Para concluir o desafio: fazer push (o commit com `subtract` já está no código) e conferir se o job *Aula 02 - Java CI* fica verde.

## 6. Discussão: como CI ajuda a manter a qualidade?

- **Feedback rápido:** o erro aparece minutos depois do commit, quando é barato corrigir.
- **Testes sempre executados:** ninguém "esquece" de rodar os testes; um *pull request* quebrado fica visível e pode ser bloqueado (branch protection).
- **Ambiente limpo e reprodutível:** evita o "na minha máquina funciona" (JDK, dependências e comandos definidos no YAML).
- **Integração em pequenos passos:** conflitos e incompatibilidades aparecem cedo, em vez do *integration hell*.
- **Base para CD:** se o build/testes são confiáveis, o deploy automático fica seguro.
- Limite: CI só é tão bom quanto os testes; testes fracos dão falsa confiança. Neste exemplo, por isso, foram incluídos os casos de borda e overflow, que o slide não cobria.

## 7. Tarefa opcional: GitHub Actions para deploy automático (pesquisa)

Para CD, o mesmo workflow ganharia um segundo job (`deploy`) com `needs: build`, executado apenas na branch principal (`if: github.ref == 'refs/heads/main'`). Pontos de atenção:

- **Segredos** (token, chave SSH, credenciais da nuvem) ficam em *Settings > Secrets* e são lidos via `${{ secrets.NOME }}`; nunca no repositório.
- **Ambientes** (`environment: production`) permitem exigir aprovação manual antes do deploy.
- Destinos comuns: GitHub Pages, GitHub Packages/Maven Central, Docker Hub/GHCR, AWS, Azure, servidores via SSH.
- Estratégias: *continuous delivery* (deploy com aprovação manual) versus *continuous deployment* (todo commit aprovado vai para produção).

Esta parte foi apenas pesquisada; **nenhum deploy foi configurado ou executado**.
