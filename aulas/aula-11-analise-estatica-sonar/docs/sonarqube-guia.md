# Guia SonarQube / SonarCloud (Aula 11, Atividades 1 e 2)

> Status: **roteiro e configuração prontos, NÃO executados** (não há Docker, servidor SonarQube nem conta
> SonarCloud nesta máquina). As métricas reais deste repositório vêm de PMD e Checkstyle
> (ver `README.md` e `evidencias/metricas-antes-depois.md`) e são um substituto, não o relatório oficial do Sonar.

## 1. O que o Sonar analisa (resumo do slide)

| Categoria | Exemplos | Onde aparece neste projeto (antes da refatoração) |
|-----------|----------|---------------------------------------------------|
| Bugs | `NullPointerException` potencial | `tipo.equals("ICMS")` com `tipo == null` (`antes/FinanceUtils`) |
| Code Smells | Métodos longos, duplicação, complexidade | `calcularImposto` (ciclomática 9, cognitiva 16), literais `"CSV"` repetidos, `+=` de String em laço |
| Vulnerabilidades | SQL Injection, senhas no código | nenhuma (projeto sem I/O) |
| Cobertura | % de linhas/ramos cobertos | JaCoCo (`mvn verify`) |

Métricas-chave: **Technical Debt** (tempo estimado para corrigir), **Maintainability Rating** (A a E; A = melhor,
baseado na razão dívida / custo de desenvolvimento), **Reliability** (bugs), **Security** (vulnerabilidades).

## 2. Subir o SonarQube local (Atividade 1)

Slide: `docker run -d --name sonarqube -p 9000:9000 sonarqube:community`. Alternativa com banco persistente:
[`docker-compose.yml`](../docker-compose.yml) (`docker compose up -d`).

1. Acesse `http://localhost:9000` (credenciais iniciais `admin` / `admin`; o Sonar **obriga a trocar** a senha no primeiro login).
2. *Projects -> Create project -> Manually*: project key `gqs-aula-11-analise-estatica`, display name `GQS Aula 11 - Análise Estática`.
3. *Analysis method -> Locally -> Generate token* (copie e guarde; ele aparece uma vez). Nunca versione o token.

## 3. Analisar o projeto Java (Atividade 2)

O plugin já está no `pom.xml` (versão atualizada; o slide usa a 3.9.1.2184, de 2022):

```xml
<plugin>
  <groupId>org.sonarsource.scanner.maven</groupId>
  <artifactId>sonar-maven-plugin</artifactId>
  <version>5.8.0.7211</version>
</plugin>
```

Comando (note `sonar.token`; `sonar.login` do slide foi removido nas versões atuais):

```bash
cd aulas/aula-11-analise-estatica-sonar
export SONAR_TOKEN=<token gerado>          # PowerShell: $env:SONAR_TOKEN='<token>'
mvn clean verify sonar:sonar \
  -Dsonar.projectKey=gqs-aula-11-analise-estatica \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.token=$SONAR_TOKEN
```

SonarCloud: `-Dsonar.host.url=https://sonarcloud.io -Dsonar.organization=<sua-org>` (e o token da sua conta).
Os `-Dsonar.*` também podem ficar em `sonar-project.properties` (exceto o token).

### Explorar o dashboard (o que identificar)

| Pergunta do slide | Onde clicar | O que esperar no "antes" (previsão a conferir no servidor) |
|-------------------|-------------|-------------------------------------------------------------|
| Maior code smell | *Issues* -> filtrar *Type = Code Smell*, ordenar por *Severity* | complexidade cognitiva de `calcularImposto` e `gerar`; literais duplicados |
| Arquivo com maior dívida técnica | *Measures -> Maintainability -> Technical Debt* (visão em lista) | `antes/FinanceUtils.java` ou `antes/RelatorioVendas.java` |
| Método mais complexo | *Measures -> Complexity -> Cyclomatic Complexity* (por arquivo) -> abrir o arquivo | `RelatorioVendas.gerar` (10) e `FinanceUtils.calcularImposto` (9) segundo o PMD |

Registre prints em `evidencias/` (`sonar-dashboard.png`, `sonar-issues.png`) quando executar.

## 4. Quality Gate mínimo do projeto (tarefa de casa)

*Quality Gates -> Create* (cópia do "Sonar way"), condições sobre **New Code**:

| Condição | Valor | Origem |
|----------|-------|--------|
| Coverage | >= 80% | slide (aulas 10 e 11) |
| Security Hotspots Reviewed | 100% | padrão Sonar way |
| Reliability Rating | A (0 bugs) | slide ("zero bugs críticos") |
| Security Rating | A (0 vulnerabilidades) | slide ("0 vulnerabilidades críticas") |
| Maintainability Rating | A | slide (dívida baixa) |
| Technical Debt (Remediation effort on New Code) | < 1h (no slide da aula 10; o da aula 11 diz "< 1 dia") | métrica custom via *Add condition* |

Associe ao projeto em *Project Settings -> Quality Gate*. O workflow
[`aula-11-sonar.yml`](../../../.github/workflows/aula-11-sonar.yml) usa `-Dsonar.qualitygate.wait=true`: se o gate
reprovar, o job falha.

> Divergência dos slides: a aula 10 diz "dívida técnica < 1h" e a aula 11 diz "< 1 dia". Adotamos 1h para o
> *new code* (mais rígido) e 1 dia como limite do projeto inteiro, e registramos aqui a escolha.

## 5. Como a dívida técnica é calculada (para interpretar o dashboard)

- Cada regra violada tem um tempo de correção (*remediation*), p. ex. 5 min para um literal duplicado; a soma é a **dívida técnica**.
- Maintainability Rating = dívida / custo de desenvolvimento do código (por padrão 30 min por linha): A <= 5%, B <= 10%, C <= 20%, D <= 50%, E > 50%.
- Nossa **estimativa local** (`scripts/resumo-metricas.ps1`) usa 5 min por violação PMD e a mesma regra de faixas;
  é aproximação, serve para mostrar a tendência antes/depois.

## 6. Critérios de sucesso do desafio (verificação no servidor)

| Critério | Como verificar no Sonar | Estimativa local (PMD/Checkstyle) |
|----------|-------------------------|-----------------------------------|
| Redução >= 50% na dívida técnica da classe | *Measures -> Technical Debt* antes x depois | ver `evidencias/metricas-antes-depois.md` |
| Aumento da nota de maintainability (ex.: C -> B) | Rating do arquivo/projeto | idem |
| Quality Gate verde | Status do gate no PR/branch | `mvn pmd:check checkstyle:check` passa no pacote `depois` |
