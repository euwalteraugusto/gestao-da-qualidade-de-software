package br.gqs.aula10.pedido;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/** Implementação em memória de {@link RepositorioPedidos}, segura para threads. */
public class RepositorioMemoria implements RepositorioPedidos {

    private final Map<String, Pedido> pedidos = new ConcurrentHashMap<>();

    @Override
    public void salvar(Pedido pedido) {
        pedidos.put(pedido.id(), pedido);
    }

    @Override
    public Optional<Pedido> buscar(String id) {
        return Optional.ofNullable(pedidos.get(id));
    }
}
