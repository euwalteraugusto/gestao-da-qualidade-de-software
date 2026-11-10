# Aula 13 - Definicao do Projeto Final (MVP) - tag `v0.1`

## Objetivo

Definir a arquitetura, implementar um MVP funcional (Java 17 + Spring Boot 3) e configurar
as metricas iniciais de qualidade.

## Decisoes de arquitetura

| Decisao | Escolha | Motivo |
|---------|---------|--------|
| Estilo | **Monolito** em camadas | Simples de desenvolver; um aluno, um servico (microsservicos trariam complexidade operacional sem ganho no escopo) |
| Backend | Spring Boot 3.3.5 + Spring Data JPA | Stack da aula |
| Banco | H2 em memoria (dev/testes) | Recomendacao da aula; PostgreSQL via Testcontainers no CI (aula 15) |
| Testes | JUnit 5 + Mockito + MockMvc | Stack da aula |
| Qualidade | JaCoCo (cobertura) + SonarQube (aula 14/15) | Stack da aula |
| Documentacao | Swagger (springdoc-openapi 2.6.0) + Markdown | Stack da aula |

Pacotes (`com.example.projeto`): `domain.model`, `domain.repository`, `interfaces.rest`.

## Criterios de MVP (checklist de entrega)

- [x] CRUD funcional de 1 entidade (`Produto`): `GET/POST /produtos`, `GET/PUT/DELETE /produtos/{id}`
- [x] 1 teste de integracao (`ProdutoControllerIT`, 4 cenarios com MockMvc + H2) e 1 unitario (`ProdutoControllerTest`, Mockito)
- [x] Relatorio JaCoCo gerado (`target/site/jacoco/index.html`)
- [x] Pipeline CI basico (`.github/workflows/projeto-final-ci.yml`)
- [ ] Push com tag `v0.1` - **a cargo do orquestrador** (`git tag -a v0.1 -m "MVP inicial"`)

## Arquivos adicionados nesta aula

```
.github/workflows/projeto-final-ci.yml
projeto-final/pom.xml
projeto-final/lombok.config
projeto-final/README.md
projeto-final/src/main/java/com/example/projeto/ProjetoApplication.java
projeto-final/src/main/java/com/example/projeto/domain/model/Produto.java
projeto-final/src/main/java/com/example/projeto/domain/repository/ProdutoRepository.java
projeto-final/src/main/java/com/example/projeto/interfaces/rest/ProdutoController.java
projeto-final/src/main/resources/application.yml
projeto-final/src/test/java/com/example/projeto/interfaces/rest/ProdutoControllerIT.java
projeto-final/src/test/java/com/example/projeto/interfaces/rest/ProdutoControllerTest.java
projeto-final/docs/aula-13/README.md
projeto-final/docs/aula-13/evidencias/*
```

O patch exato desta aula esta em [`mudancas.patch`](mudancas.patch) (diff a partir do repositorio vazio).

## Correcoes em relacao aos slides

| Slide | Problema | Correcao |
|-------|----------|----------|
| Pacote `com.example.projeto.interface.rest` | `interface` e palavra reservada do Java: o pacote nao compila | Pacote renomeado para `interfaces.rest` |
| Plugin JaCoCo sem `<executions>` | Sem o goal `prepare-agent` o agente nao instrumenta os testes e `jacoco:report` nao gera nada (falta `jacoco.exec`) | Adicionadas as execucoes `prepare-agent` e `report` (fase `verify`); versao 0.8.12 |
| Entidade com `@Data` | `equals/hashCode` sobre todos os campos (inclusive `id` gerado) e problematico em entidades JPA | `@Getter @Setter @ToString @NoArgsConstructor` |
| `ProdutoControllerIT` sem isolamento | `deveListarProdutosVazios` depende da ordem dos testes | `@Transactional` na classe (rollback por teste, dica da aula 14) |
| Testes `*IT` | O Surefire por padrao so executa `*Test`; o IT do slide nunca rodaria no `mvn verify` | `includes` do Surefire com `**/*IT.java` |
| `actions/checkout@v3` | Versao antiga | Actions fixadas em `@v4` |

## Como reproduzir

```bash
cd projeto-final
mvn -B clean verify
java -jar target/projeto-final.jar
curl http://localhost:8080/produtos
```

## Evidencias

- [`evidencias/mvn-verify.txt`](evidencias/mvn-verify.txt) - saida do `mvn clean verify`
- [`evidencias/jacoco-resumo.txt`](evidencias/jacoco-resumo.txt) - cobertura por classe
- [`evidencias/curl-endpoints.txt`](evidencias/curl-endpoints.txt) - CRUD executado com `curl` contra a app rodando
- [`evidencias/jacoco-relatorio.png`](evidencias/jacoco-relatorio.png) - captura do relatorio HTML do JaCoCo

Resultado: **5 testes, 0 falhas; cobertura 100% de linhas** (16/16) - a entidade `Produto` so tem
codigo gerado pelo Lombok, marcado com `@Generated` e ignorado pelo JaCoCo.
