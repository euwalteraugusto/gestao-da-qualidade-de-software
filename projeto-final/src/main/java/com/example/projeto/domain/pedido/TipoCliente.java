package com.example.projeto.domain.pedido;

/**
 * Tipos de cliente. Substitui a {@code String tipoCliente} do codigo legado
 * (code smell "Primitive Obsession": qualquer texto era aceito e erros de digitacao
 * caiam silenciosamente no "sem desconto").
 */
public enum TipoCliente {
    REGULAR,
    VIP,
    PREMIUM
}
