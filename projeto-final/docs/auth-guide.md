# Guia de Autenticacao (JWT) - API de Produtos

Entrega da aula 18 ("Testes Postman documentados em `docs/auth-guide.md`") + refresh token
(tarefa pos-aula). Todos os comandos abaixo foram executados de verdade contra a aplicacao local;
as saidas estao em [`aula-18/evidencias/curl-endpoints.txt`](aula-18/evidencias/curl-endpoints.txt).

## Visao geral

```
Client --(1) POST /auth/login {username,password}--> AuthAPI
Client <--(2) {accessToken, refreshToken} ------------ AuthAPI
Client --(3) GET /api/produtos  Authorization: Bearer <accessToken> --> ResourceAPI
Client <--(4) dados protegidos -------------------------------------- ResourceAPI
Client --(5) POST /auth/refresh {refreshToken} --> AuthAPI  (quando o access token expira)
```

| Token | Validade (padrao) | Claims | Uso |
|-------|-------------------|--------|-----|
| access | 15 min (`app.jwt.access-expiration`) | `sub`, `roles`, `typ=access`, `iss`, `jti` | Header `Authorization: Bearer ...` em `/api/**` |
| refresh | 7 dias (`app.jwt.refresh-expiration`) | `sub`, `typ=refresh`, `iss`, `jti` | Somente no corpo de `POST /auth/refresh` |

Assinatura HS256 (jjwt 0.12.6). Um refresh token **nao** e aceito como access token e vice-versa.
No refresh os papeis sao recarregados do cadastro e um **novo par** e emitido (rotacao).

## Usuarios de desenvolvimento (perfil `dev`)

| Usuario | Senha (somente dev) | Papeis | Pode |
|---------|---------------------|--------|------|
| `admin` | `admin-dev-2026` | `ADMIN`, `USER` | ler e alterar o catalogo, acessar `/produtos/novo` |
| `cliente` | `cliente-dev-2026` | `USER` | listar/buscar produtos e calcular pedidos |

As senhas **nao** estao no codigo nem na configuracao: `application-dev.yml` guarda apenas os
**hashes BCrypt** (custo 10). Para gerar um hash novo:

```bash
# com o Spring Boot CLI ou qualquer ferramenta BCrypt; exemplo em Java (jshell) com spring-security-crypto no classpath:
new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().encode("minha-senha")
```

## Configuracao por ambiente

| Propriedade | Variavel de ambiente | dev (padrao) | prod |
|-------------|----------------------|--------------|------|
| `app.jwt.secret` | `JWT_SECRET` | chave de desenvolvimento em `application-dev.yml` | **obrigatoria** (>= 32 bytes); sem ela a app nao sobe |
| `app.security.admin.password-hash` | `ADMIN_PASSWORD_HASH` | hash de `admin-dev-2026` | **obrigatoria** |
| `app.security.cliente.password-hash` | `CLIENTE_PASSWORD_HASH` | hash de `cliente-dev-2026` | **obrigatoria** |
| `app.security.admin.username` / `cliente.username` | `ADMIN_USERNAME` / `CLIENTE_USERNAME` | `admin` / `cliente` | opcional |

Gerar uma chave forte: `openssl rand -base64 48`.

## Testes (curl / Postman)

Os mesmos passos podem ser importados no Postman (cada bloco = uma request; use uma variavel de
ambiente `{{token}}` com o `accessToken` retornado no passo 1).

### 1. Login

```http
POST /auth/login HTTP/1.1
Content-Type: application/json

{ "username": "admin", "password": "admin-dev-2026" }
```

```bash
curl -s -X POST http://localhost:8080/auth/login -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin-dev-2026"}'
# 200 {"accessToken":"eyJ...","refreshToken":"eyJ...","tokenType":"Bearer","expiresIn":900}
```

Postman: aba *Tests* -> `pm.environment.set("token", pm.response.json().accessToken);`

### 2. Acesso a recurso protegido

```http
GET /api/produtos HTTP/1.1
Authorization: Bearer {{token}}
```

| Cenario | Resultado esperado |
|---------|--------------------|
| Sem header `Authorization` | `401` `{"title":"Nao autenticado",...}` |
| Token mal formado / assinatura errada / expirado | `401` |
| Token valido de `cliente` em `GET /api/produtos` | `200` |
| Token valido de `cliente` em `POST /api/produtos` | `403` `{"title":"Acesso negado",...}` |
| Token valido de `admin` em `POST /api/produtos` | `201` |
| Senha errada em `/auth/login` | `401` `{"detail":"Usuario ou senha invalidos"}` |

### 3. Refresh token

```bash
curl -s -X POST http://localhost:8080/auth/refresh -H "Content-Type: application/json" \
  -d '{"refreshToken":"<refreshToken do passo 1>"}'
# 200 com novo accessToken e novo refreshToken
```

Enviar um **access** token em `/auth/refresh` -> `401`; enviar um **refresh** token como Bearer -> `401`.

### 4. Swagger UI

`http://localhost:8080/swagger-ui.html` e publico; clique em **Authorize** e cole o `accessToken`
(sem o prefixo `Bearer`) para testar os endpoints protegidos.

## Interface web

As paginas Thymeleaf (`/produtos`, `/cep`) usam **login por formulario** (`/login`) e sessao, com
CSRF ativo. O botao "Novo produto" so aparece para `ADMIN` (`sec:authorize`) e `/produtos/novo`
exige `ADMIN` (`@PreAuthorize`) - controle de papeis no frontend (tarefa pos-aula 18).
O AJAX da pagina chama `/api/produtos` com o cookie de sessao (leitura permitida); escrita em `/api/**`
por sessao exige token CSRF.

## Regras de autorizacao (resumo)

| Rota | Regra |
|------|-------|
| `/auth/**` | publica |
| `/swagger-ui/**`, `/v3/api-docs/**`, `/login`, `/css/**` | publica |
| `GET /api/**`, `POST /api/pedidos/total` | autenticado (`USER` ou `ADMIN`) |
| `POST/PUT/DELETE /api/produtos/**` | `@PreAuthorize("hasRole('ADMIN')")` |
| `/produtos/novo`, `/produtos/cadastrar` (web) | `@PreAuthorize("hasRole('ADMIN')")` |
| `/admin/**` | `hasRole('ADMIN')` (regra do slide, reservada) |
| demais paginas | autenticado (sessao) |
