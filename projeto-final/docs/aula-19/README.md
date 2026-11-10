# Aula 19 - Deploy e Monitoramento (Heroku / Actuator)

> A aula 19 nao tem PDF: o conteudo veio de `Aula_19_IQS.java` (system.properties, Procfile, comandos
> Heroku, Actuator, `ApiExternaHealthIndicator`, `DataSourceHealthIndicator` e "Como Deployar").

## Entregas

| Item | Onde |
|------|------|
| `system.properties` (`java.runtime.version=17`) | `projeto-final/system.properties` |
| `Procfile` corrigido | `projeto-final/Procfile` |
| Spring Boot Actuator (`health`, `info`, `metrics`) | `pom.xml`, `application.yml` |
| **Health indicator customizado** da API externa (ViaCEP) | `health/ApiExternaHealthIndicator.java` -> componente `apiExterna` |
| Health do banco com `SELECT 1` (slide) | `config/HealthConfig.java` -> componente `db` |
| Perfil de producao (segredos obrigatorios, Postgres via `JDBC_DATABASE_URL`, detalhes do health so para ADMIN) | `application-prod.yml` |
| Seguranca dos endpoints do Actuator | `health`/`info` publicos; `metrics` e demais exigem `ADMIN` (JWT) |
| Smoke test pos-deploy no CI (`/actuator/health`) | job `deploy-heroku` em `.github/workflows/projeto-final-ci.yml` |
| Driver PostgreSQL em runtime (antes so teste) | `pom.xml` |

## Como Deployar

### Heroku (Git) - fluxo do slide

Este repositorio e um monorepo (`projeto-final/` e uma subpasta), entao o buildpack Java precisa
saber onde esta o `pom.xml`:

```bash
heroku login
heroku create meu-app-produtos
heroku buildpacks:add -a meu-app-produtos https://github.com/lstoll/heroku-buildpack-monorepo
heroku buildpacks:add -a meu-app-produtos heroku/java
heroku config:set -a meu-app-produtos APP_BASE=projeto-final \
  JWT_SECRET="$(openssl rand -base64 48)" \
  ADMIN_PASSWORD_HASH='<hash BCrypt>' CLIENTE_PASSWORD_HASH='<hash BCrypt>'
heroku addons:create -a meu-app-produtos heroku-postgresql:essential-0   # opcional (sem ele usa H2 em memoria)
git push heroku main
heroku logs --tail -a meu-app-produtos
curl https://<app>.herokuapp.com/actuator/health
# {"status":"UP"}   (em prod os componentes so aparecem para ADMIN autenticado)
```

O `Procfile` sobe o jar com `--spring.profiles.active=prod`; o `system.properties` fixa o Java 17.

### Heroku (CI) - deploy do jar

O job `deploy-heroku` do workflow `projeto-final-ci.yml` (criado na aula 15) roda apenas em push na
`main` e **somente se** os secrets `HEROKU_API_KEY` e `HEROKU_APP_NAME` existirem. Nesta aula ele
ganhou um *smoke test*: se a variable `HEROKU_APP_URL` estiver definida, faz `curl` em
`$HEROKU_APP_URL/actuator/health` e falha o job se nao responder `UP`. As variaveis de ambiente da app
(`SPRING_PROFILES_ACTIVE=prod`, `JWT_SECRET`, hashes) devem ser configuradas no Heroku
(`heroku config:set`) - no `deploy:jar` o Procfile do repositorio nao e usado, por isso o perfil vem
da config var e a porta de `$PORT` (`server.port: ${PORT:8080}` no perfil prod).

### AWS (alternativa, documentada)

Elastic Beanstalk (plataforma *Corretto 17*): `mvn -B clean verify`, `eb init`, `eb create`, e enviar
`target/projeto-final.jar`; definir `SERVER_PORT=5000`, `SPRING_PROFILES_ACTIVE=prod`, `JWT_SECRET`,
`ADMIN_PASSWORD_HASH`, `CLIENTE_PASSWORD_HASH` e, com RDS PostgreSQL, `JDBC_DATABASE_URL`,
`JDBC_DATABASE_USERNAME`, `JDBC_DATABASE_PASSWORD`. Health check do load balancer: `/actuator/health`.

### Nao executado (e por que)

- **Deploy no Heroku/AWS nao foi executado**: nao ha conta (e o projeto nao usa contas externas).
  Os arquivos (`Procfile`, `system.properties`, `application-prod.yml`, job de deploy condicional)
  estao prontos; o perfil `prod` foi **executado localmente** simulando o Heroku (variavel `PORT`,
  segredos por variavel de ambiente) - ver evidencias.

## Arquivos adicionados/alterados nesta aula

```
M .github/workflows/projeto-final-ci.yml             (smoke test /actuator/health apos o deploy)
A projeto-final/Procfile
A projeto-final/system.properties
M projeto-final/pom.xml                               (1.0.0; actuator; postgresql runtime)
M projeto-final/README.md
M projeto-final/src/main/resources/application.yml    (management.*, info.*)
A projeto-final/src/main/resources/application-prod.yml
A projeto-final/src/main/java/com/example/projeto/health/ApiExternaHealthIndicator.java
A projeto-final/src/main/java/com/example/projeto/config/HealthConfig.java
M projeto-final/src/main/java/com/example/projeto/config/SecurityConfig.java (regras /actuator/**)
A projeto-final/src/test/java/com/example/projeto/health/{ApiExternaHealthIndicatorTest,ActuatorIT}.java
A projeto-final/docs/aula-19/README.md
A projeto-final/docs/aula-19/evidencias/*
```

Patch exato: [`mudancas.patch`](mudancas.patch).

## Correcoes em relacao ao material

| Material | Problema | Correcao |
|----------|----------|----------|
| `web: java -jar target/ *.jar --server.port=$PORT` | O espaco em `target/ *.jar` vira dois argumentos (`target/` e o glob do diretorio atual): o Java tenta abrir `target/` como jar e a app nao sobe | `java -Dserver.port=$PORT $JAVA_OPTS -jar target/projeto-final.jar --spring.profiles.active=prod` (`finalName` fixo no pom) |
| `return Health.up().build();  # Status UP` | Comentario `#` dentro de Java nao compila | Comentarios Java; `checkApiExterna()` (inexistente) implementado via `CepService` |
| `show-details: always` | Em producao expoe detalhes internos (erros, banco) a qualquer um | `always` em dev; `when-authorized` + role `ADMIN` em `prod` |
| `include: health,info,metrics` sem seguranca | `metrics` publico vaza informacao operacional | `health`/`info` publicos; `/actuator/**` restante exige `ADMIN` |
| Bean `dbHealth` com `DataSourceHealthIndicator` | Com outro nome o Boot mantem o indicador automatico e o banco aparece duas vezes (`db` e `dbHealth`) | Bean `dbHealthIndicator` (substitui o automatico) |
| `info` vazio | Desde o Boot 2.6 `info.*` so aparece com `management.info.env.enabled=true` | Habilitado, com nome/versao do pom |
| (achado durante os testes) `cep-referencia: 01001000` no YAML | YAML 1.1 le numeros com zero a esquerda como octal: o CEP virava `262656` e o health ficava DOWN (o `ActuatorIT` pegou) | Valor entre aspas (`'01001000'`) |
| `git push heroku main` num monorepo | O buildpack nao encontra o `pom.xml` na raiz | Buildpack monorepo + `APP_BASE=projeto-final` (ou deploy do jar pelo CI) |

## Evidencias

- [`evidencias/mvn-verify.txt`](evidencias/mvn-verify.txt), [`evidencias/jacoco-resumo.txt`](evidencias/jacoco-resumo.txt), [`evidencias/jacoco-relatorio.png`](evidencias/jacoco-relatorio.png)
- [`evidencias/curl-endpoints.txt`](evidencias/curl-endpoints.txt) - `/actuator/health` (com `apiExterna` e `db`), `info`, `metrics` 401/403/200
- [`evidencias/prod-sem-segredos.txt`](evidencias/prod-sem-segredos.txt) - perfil `prod` sem `JWT_SECRET`: a app se recusa a subir
- [`evidencias/prod-local.txt`](evidencias/prod-local.txt) - perfil `prod` com segredos por variavel de ambiente e `PORT` (simulando Heroku)

Resultado final do projeto: **85 testes, 0 falhas, 1 skipped** (`ProdutoPostgresIT`, roda so com
Docker/CI); cobertura JaCoCo **100% de linhas (343/343), 100% de instrucoes, 91,9% de branches**;
`jacoco:check` (linhas >= 80%, branches >= 70%) aprovado. Health local: `apiExterna` UP (ViaCEP real),
`db` UP com `SELECT 1`. Perfil `prod` sem segredos: **falha na subida** (`APPLICATION FAILED TO START`,
`app.security.admin.passwordHash` em branco); com segredos: sobe na porta de `PORT`, health anonimo so
mostra `{"status":"UP"}` e detalhes so com token ADMIN; Swagger desligado (404).
Caminhos locais da maquina foram mascarados nas evidencias.
