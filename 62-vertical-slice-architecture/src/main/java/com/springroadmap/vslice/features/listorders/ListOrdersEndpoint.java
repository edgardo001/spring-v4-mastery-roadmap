package com.springroadmap.vslice.features.listorders;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoint HTTP GET /api/orders?status=XXX
 *
 * <p>La query string {@code status} es opcional gracias a
 * {@code required = false}. Si no viene, el handler devuelve todas.</p>
 */
@RestController
public class ListOrdersEndpoint {

    private final ListOrdersHandler handler;

    public ListOrdersEndpoint(ListOrdersHandler handler) {
        this.handler = handler;
    }

    @GetMapping("/api/orders")
    public List<OrderSummary> list(@RequestParam(name = "status", required = false) String status) {
        return handler.handle(status);
    }
}
