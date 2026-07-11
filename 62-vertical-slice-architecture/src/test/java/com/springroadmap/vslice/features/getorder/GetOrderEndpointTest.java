package com.springroadmap.vslice.features.getorder;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.springroadmap.vslice.shared.OrderStore;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GetOrderEndpointTest {

    @Test
    void getOrdenExistenteDevuelve200() throws Exception {
        OrderStore store = new OrderStore();
        OrderStore.StoredOrder saved = store.save("Ana", new BigDecimal("7.77"), "CREATED");
        GetOrderHandler handler = new GetOrderHandler(store);
        GetOrderEndpoint endpoint = new GetOrderEndpoint(handler);
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(endpoint).build();

        mockMvc.perform(get("/api/orders/" + saved.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customer").value("Ana"))
                .andExpect(jsonPath("$.status").value("CREATED"));
    }

    @Test
    void getOrdenInexistenteDevuelve404() throws Exception {
        OrderStore store = new OrderStore();
        GetOrderHandler handler = new GetOrderHandler(store);
        GetOrderEndpoint endpoint = new GetOrderEndpoint(handler);
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(endpoint).build();

        mockMvc.perform(get("/api/orders/999"))
                .andExpect(status().isNotFound());
    }
}
