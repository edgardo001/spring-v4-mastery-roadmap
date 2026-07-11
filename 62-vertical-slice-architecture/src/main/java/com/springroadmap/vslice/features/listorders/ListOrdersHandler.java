package com.springroadmap.vslice.features.listorders;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.springroadmap.vslice.shared.OrderStore;

/**
 * Handler de la feature "listar ordenes filtradas por status" — Query.
 */
@Component
public class ListOrdersHandler {

    private final OrderStore store;

    public ListOrdersHandler(OrderStore store) {
        this.store = store;
    }

    /**
     * Devuelve la lista de resumenes de ordenes.
     * @param status filtro opcional; null o vacio => todas.
     *
     * <p><b>ANTES (Java 8):</b> for clasico con ArrayList.
     * <b>AHORA (Java 21):</b> podria hacerse con
     * {@code stream().map(...).toList()} — se muestra el for tradicional
     * para que el lector reconozca la equivalencia.</p>
     */
    public List<OrderSummary> handle(String status) {
        List<OrderStore.StoredOrder> raw = store.findAll(status);
        List<OrderSummary> result = new ArrayList<>();
        for (OrderStore.StoredOrder o : raw) {
            result.add(new OrderSummary(o.id(), o.customer(), o.status()));
        }
        return result;
    }
}
