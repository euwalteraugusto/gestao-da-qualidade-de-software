# Gestão da Qualidade de Software

Entregas da disciplina **Introdução à Qualidade de Software (IQS) com Java Avançado** — 20 aulas de 2h.
Cada aula está em sua própria pasta, com `README.md` (resumo, exercícios, o que foi feito, correções dos slides, discussões), código, testes e `evidencias/` (logs e prints).

## Índice

| Aula | Tema | Pasta |
|---|---|---|
| 1 | Introdução à Qualidade / Clean Code | [aulas/aula-01-introducao-qualidade](aulas/aula-01-introducao-qualidade) |
| 2 | Metodologias ágeis e CI/CD (GitHub Actions) | [aulas/aula-02-metodologias-ageis-ci](aulas/aula-02-metodologias-ageis-ci) |
| 3 | Padrões de codificação, Checkstyle, PMD | [aulas/aula-03-padroes-codificacao](aulas/aula-03-padroes-codificacao) |
| 4 | SOLID e Design Patterns | [aulas/aula-04-solid-design-patterns](aulas/aula-04-solid-design-patterns) |
| 5 | Exceções e Logging | [aulas/aula-05-excecoes-logging](aulas/aula-05-excecoes-logging) |
| 6 | Testes unitários com JUnit 5 | [aulas/aula-06-junit5](aulas/aula-06-junit5) |
| 7 | Mockito, Testcontainers, JaCoCo | [aulas/aula-07-mockito-integracao-jacoco](aulas/aula-07-mockito-integracao-jacoco) |
| 8 | BDD com Cucumber | [aulas/aula-08-bdd-cucumber](aulas/aula-08-bdd-cucumber) |
| 9 | Git Flow, PRs e Code Review | [aulas/aula-09-git-flow-code-review](aulas/aula-09-git-flow-code-review) |
| 10 | CI/CD (GitHub Actions, Jenkins) | [aulas/aula-10-ci-cd-jenkins](aulas/aula-10-ci-cd-jenkins) |
| 11 | Análise estática e SonarQube | [aulas/aula-11-analise-estatica-sonar](aulas/aula-11-analise-estatica-sonar) |
| 12 | Documentação (Javadoc, OpenAPI) | [aulas/aula-12-documentacao](aulas/aula-12-documentacao) |
| 13–19 | Projeto final (Spring Boot, tema Produto) | [projeto-final](projeto-final) — docs por aula em `projeto-final/docs/aula-NN` |
| 20 | Apresentação do projeto final | sem material de exercício |

## Como rodar

Requisitos: JDK 17 e Maven 3.9+. Cada aula é um projeto Maven independente:

```bash
cd aulas/aula-06-junit5
mvn -B clean test
```

Os testes com Testcontainers exigem Docker; sem Docker eles são ignorados localmente e rodam no GitHub Actions.

## Transparência

Cada README de aula registra o que **não** foi executado (contas externas como SonarQube, Sentry e Heroku, Jenkins/Kubernetes, atividades em grupo) e as correções feitas em trechos dos slides (versões antigas, bugs). Atividades em dupla/grupo foram simuladas por um único aluno e estão marcadas como tal.
