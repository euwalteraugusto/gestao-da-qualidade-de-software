package com.example.projeto.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

/**
 * Documentacao Swagger/OpenAPI com o esquema de seguranca Bearer JWT
 * (botao "Authorize" no Swagger UI - cole o accessToken de /auth/login).
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(title = "API de Produtos - Projeto Final IQS", version = "v1",
                description = "CRUD de produtos, busca por nome, calculo de pedidos e autenticacao JWT"),
        security = @SecurityRequirement(name = "bearerAuth"))
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT")
public class OpenApiConfig {
}
