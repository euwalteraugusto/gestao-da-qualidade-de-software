# Projeto Final - API de Produtos (IQS, aulas 13 a 19)

Projeto unico em Spring Boot 3 (Java 17, Maven) que evolui aula a aula, do MVP de CRUD de
`Produto` ate autenticacao JWT, monitoramento e deploy. Toda a documentacao por aula fica em
[`docs/`](docs/).

| Aula | Tema | Documentacao |
|------|------|--------------|
| 13 | MVP: CRUD de Produto, testes, JaCoCo, CI basico (tag `v0.1`) | [docs/aula-13](docs/aula-13/README.md) |

## Como executar

Pre-requisitos: JDK 17 e Maven 3.9+.

```bash
cd projeto-final
mvn clean verify          # compila, roda testes e gera o relatorio JaCoCo
mvn spring-boot:run       # sobe a API em http://localhost:8080
```

- Swagger UI: http://localhost:8080/swagger-ui.html
- Relatorio de cobertura: `target/site/jacoco/index.html`
