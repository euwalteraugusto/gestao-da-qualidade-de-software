# Projeto Final - API de Produtos (IQS, aulas 13 a 19)

Projeto unico em **Spring Boot 3.3.5 (Java 17, Maven)** que evolui aula a aula, do MVP de CRUD de
`Produto` ate autenticacao JWT, monitoramento e deploy. A documentacao de cada aula (o que foi feito,
arquivos alterados, correcoes em relacao aos slides e evidencias de execucao) fica em [`docs/`](docs/).

| Aula | Tema | Documentacao |
|------|------|--------------|
| 13 | MVP: CRUD de Produto, testes, JaCoCo, CI basico (tag `v0.1`) | [docs/aula-13](docs/aula-13/README.md) |
| 14 | User Stories, busca por nome (US-001), Definition of Done, SonarQube | [docs/aula-14](docs/aula-14/README.md), [user-stories.md](docs/aula-14/user-stories.md) |
| 15 | Sprint 1: CI/CD, camada de servico, validacao (US-002), Testcontainers, JaCoCo >= 80% | [docs/aula-15](docs/aula-15/README.md) |
| 16 | Refatoracao: Strategy + Factory + Decorator no calculo de pedidos (US-003) | [docs/aula-16](docs/aula-16/README.md), [decisions.md](docs/decisions.md) |
| 17 | Frontend Thymeleaf + AJAX, API externa ViaCEP, tratamento de erros (API em `/api/**`) | [docs/aula-17](docs/aula-17/README.md) |
| 18 | Spring Security 6 + JWT (login, roles, refresh token), roles no frontend | [docs/aula-18](docs/aula-18/README.md), [auth-guide.md](docs/auth-guide.md) |
| 19 | Deploy (Procfile, system.properties, perfil prod) e monitoramento (Actuator + health customizado) | [docs/aula-19](docs/aula-19/README.md) |

## Arquitetura

Monolito em camadas (decisao da aula 13), pacote base `com.example.projeto`:

| Pacote | Responsabilidade |
|--------|------------------|
| `domain.model`, `domain.repository`, `domain.service` | Entidade `Produto`, Spring Data JPA, regras de negocio |
| `domain.pedido` (+ `desconto`, `frete`) | Calculo de pedidos: **Strategy**, **Factory**, **Decorator** (aula 16) |
| `interfaces.rest` | API REST JSON (`/api/produtos`, `/api/pedidos`), erros em ProblemDetail (RFC 7807) |
| `web` | Paginas Thymeleaf (`/produtos`, `/cep`) |
| `integration` | Cliente HTTP da API externa ViaCEP |
| `auth`, `config` | JWT, Spring Security (2 cadeias), Swagger, frete, health |
| `health` | `ApiExternaHealthIndicator` (Actuator) |

### Endpoints principais

| Metodo | Caminho | Acesso |
|--------|---------|--------|
| POST | `/auth/login`, `/auth/refresh` | publico |
| GET | `/api/produtos`, `/api/produtos/{id}`, `/api/produtos/buscar?nome=` | USER/ADMIN (JWT) |
| POST/PUT/DELETE | `/api/produtos`, `/api/produtos/{id}` | ADMIN (JWT) |
| POST | `/api/pedidos/total` | USER/ADMIN (JWT) |
| GET | `/actuator/health`, `/actuator/info` | publico |
| GET | `/actuator/metrics` | ADMIN (JWT) |
| GET | `/produtos`, `/cep` (paginas) | login por formulario |
| GET | `/swagger-ui.html`, `/v3/api-docs` | publico (desligado em `prod` por padrao) |

Detalhes de autenticacao, usuarios de desenvolvimento e roteiro curl/Postman: [docs/auth-guide.md](docs/auth-guide.md).

## Como executar

Pre-requisitos: JDK 17 e Maven 3.9+ (Docker opcional - so para o teste com Testcontainers).

```bash
cd projeto-final
mvn clean verify                 # compila, testa, gera JaCoCo e FALHA se cobertura < 80% (linhas)
mvn spring-boot:run              # perfil dev (padrao) em http://localhost:8080
```

- Paginas: http://localhost:8080/produtos (login `admin` / `admin-dev-2026` - somente dev)
- Swagger UI: http://localhost:8080/swagger-ui.html
- Health: http://localhost:8080/actuator/health
- Cobertura: `target/site/jacoco/index.html`

Producao (perfil `prod`, exige segredos por variavel de ambiente - ver [aula 19](docs/aula-19/README.md)):

```bash
export JWT_SECRET="$(openssl rand -base64 48)" ADMIN_PASSWORD_HASH='<bcrypt>' CLIENTE_PASSWORD_HASH='<bcrypt>'
java -jar target/projeto-final.jar --spring.profiles.active=prod
```

## Qualidade

| Item | Situacao |
|------|----------|
| Testes | 80+ testes JUnit 5 (unitarios com Mockito, `@DataJpaTest`, `@RestClientTest`, MockMvc de integracao, Testcontainers/PostgreSQL) |
| Cobertura | JaCoCo com `check` no `mvn verify`: linhas >= 80% e branches >= 70% (atual: ~100% linhas) |
| CI | `.github/workflows/projeto-final-ci.yml` (build, testes com Docker/Testcontainers, artefatos, deploy condicional) |
| Analise estatica | `.github/workflows/projeto-final-sonar.yml` (SonarQube/SonarCloud, so com secret `SONAR_TOKEN`) |
| Decisoes de projeto | [docs/decisions.md](docs/decisions.md) |

## Definition of Done (DoD)

Uma User Story so e considerada **pronta** quando:

- [ ] Todos os criterios de aceitacao da US estao atendidos e demonstraveis
- [ ] Codigo revisado por pelo menos 1 colega (Pull Request aprovado e vinculado a issue)
- [ ] Testes automatizados escritos (unitario e/ou integracao) e `mvn clean verify` verde no CI
- [ ] Cobertura de testes > 80% (JaCoCo) - verificada automaticamente pelo `jacoco:check`
- [ ] Passa no SonarQube sem bugs criticos nem vulnerabilidades (quality gate verde)
- [ ] Documentacao Swagger atualizada (endpoints novos visiveis em `/swagger-ui.html`)
- [ ] README/docs da aula atualizados com as mudancas e evidencias
- [ ] Nenhum segredo (senha, token, chave) commitado no repositorio

User Stories do projeto: [docs/aula-14/user-stories.md](docs/aula-14/user-stories.md).

## Historico por aula (para tags/commits)

Cada `docs/aula-NN/mudancas.patch` contem o diff exato (formato `git diff --binary`, caminhos a partir
da raiz do repositorio) do estado da aula anterior para o estado da aula NN, incluindo
`.github/workflows/projeto-final-*.yml`. A partir de um repositorio sem `projeto-final/`:

```bash
for n in 13 14 15 16 17 18 19; do
  git apply --binary "/caminho/para/copia/projeto-final/docs/aula-$n/mudancas.patch"
  git add -A && git commit -m "Projeto final: aula $n"
  [ "$n" = 13 ] && git tag -a v0.1 -m "MVP inicial"
done
```

(Os proprios arquivos `mudancas.patch` nao fazem parte dos patches; copie-os por ultimo se quiser
versiona-los.)
