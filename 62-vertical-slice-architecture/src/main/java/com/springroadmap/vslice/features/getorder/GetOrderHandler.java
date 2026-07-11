package com.springroadmap.vslice.features.getorder;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.springroadmap.vslice.shared.OrderStore;

/**
 * Handler de la feature "obtener orden por id" — es una Query (solo lectura).
 *
 * <p><b>PREGUNTA DE ALUMNO — "¿Optional se pasa como retorno? Yo aprendi
 * que Optional no se usa asi."</b>
 * Optional como retorno de un metodo publico es exactamente su uso previsto:
 * comunica al llamador "puede que no haya resultado". Lo que NO se recomienda
 * es Optional como CAMPO de una clase o como PARAMETRO de metodo.</p>
 */
@Component
public class GetOrderHandler {

    private final OrderStore store;

    public GetOrderHandler(OrderStore store) {
        this.store = store;
    }

    /**
     * Busca por id. Devuelve Optional.empty() si no existe.
     *
     * <p><b>ANTES (Java 8):</b>
     * <pre>
     *   StoredOrder o = data.get(id);
     *   if (o == null) return null;
     *   return new GetOrderResponse(o.getId(), ...);
     * </pre>
     * <b>AHORA (Java 21):</b> {@code .map(...)} solo se ejecuta si hay valor.
     * </p>
     */
    public Optional<GetOrderResponse> handle(Long id) {
        return store.findById(id)
                .map(o -> new GetOrderResponse(o.id(), o.customer(), o.amount(), o.status()));
    }
}
