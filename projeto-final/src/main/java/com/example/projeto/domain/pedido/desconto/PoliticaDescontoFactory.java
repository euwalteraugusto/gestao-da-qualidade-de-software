package com.example.projeto.domain.pedido.desconto;

import com.example.projeto.domain.pedido.TipoCliente;
import org.springframework.stereotype.Component;


import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * <b>Factory</b>: centraliza a escolha (criacao/selecao) da {@link PoliticaDesconto}
 * adequada a cada {@link TipoCliente}.
 * <p>
 * Recebe todas as estrategias registradas no Spring; adicionar um novo desconto
 * e so criar um novo {@code @Component} - a factory e o servico nao mudam.
 * A validacao no construtor garante, na subida da aplicacao, que todo tipo de
 * cliente tem exatamente uma estrategia (falha rapida em vez de erro em producao).
 */
@Component
public class PoliticaDescontoFactory {

    private final Map<TipoCliente, PoliticaDesconto> politicas = new EnumMap<>(TipoCliente.class);

    public PoliticaDescontoFactory(List<PoliticaDesconto> estrategias) {
        for (PoliticaDesconto estrategia : estrategias) {
            PoliticaDesconto anterior = politicas.put(estrategia.tipoCliente(), estrategia);
            if (anterior != null) {
                throw new IllegalStateException("Mais de uma politica para " + estrategia.tipoCliente());
            }
        }
        for (TipoCliente tipo : TipoCliente.values()) {
            if (!politicas.containsKey(tipo)) {
                throw new IllegalStateException("Nenhuma politica de desconto para " + tipo);
            }
        }
    }

    public PoliticaDesconto obter(TipoCliente tipoCliente) {
        return politicas.get(tipoCliente == null ? TipoCliente.REGULAR : tipoCliente);
    }
}
