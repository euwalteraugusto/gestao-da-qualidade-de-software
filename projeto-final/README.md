# Projeto Final - API de Produtos (IQS, aulas 13 a 19)

Projeto unico em Spring Boot 3 (Java 17, Maven) que evolui aula a aula, do MVP de CRUD de
`Produto` ate autenticacao JWT, monitoramento e deploy. Toda a documentacao por aula fica em
[`docs/`](docs/).

| Aula | Tema | Documentacao |
|------|------|--------------|
| 13 | MVP: CRUD de Produto, testes, JaCoCo, CI basico (tag `v0.1`) | [docs/aula-13](docs/aula-13/README.md) |
| 14 | User Stories, busca por nome (US-001), Definition of Done, SonarQube | [docs/aula-14](docs/aula-14/README.md) |
| 15 | Sprint 1: CI/CD, camada de servico, validacao (US-002), Testcontainers, JaCoCo >= 80% | [docs/aula-15](docs/aula-15/README.md) |

## Como executar

Pre-requisitos: JDK 17 e Maven 3.9+.

```bash
cd projeto-final
mvn clean verify          # compila, roda testes e gera o relatorio JaCoCo
mvn spring-boot:run       # sobe a API em http://localhost:8080
```

- Swagger UI: http://localhost:8080/swagger-ui.html
- Relatorio de cobertura: `target/site/jacoco/index.html`

## Definition of Done (DoD)

Uma User Story so e considerada **pronta** quando:

- [ ] Todos os criterios de aceitacao da US estao atendidos e demonstraveis
- [ ] Codigo revisado por pelo menos 1 colega (Pull Request aprovado e vinculado a issue)
- [ ] Testes automatizados escritos (unitario e/ou integracao) e `mvn clean verify` verde no CI
- [ ] Cobertura de testes > 80% (JaCoCo)
- [ ] Passa no SonarQube sem bugs criticos nem vulnerabilidades (quality gate verde)
- [ ] Documentacao Swagger atualizada (endpoints novos visiveis em `/swagger-ui.html`)
- [ ] README/docs da aula atualizados com as mudancas e evidencias
- [ ] Nenhum segredo (senha, token, chave) commitado no repositorio

User Stories do projeto: [docs/aula-14/user-stories.md](docs/aula-14/user-stories.md).
