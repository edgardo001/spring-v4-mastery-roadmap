package com.springroadmap.restadv.controller;

import com.springroadmap.restadv.domain.Product;
import com.springroadmap.restadv.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * MockMvc standalone: construye el controller a mano con el repo mockeado.
 * Registra `PageableHandlerMethodArgumentResolver` porque el controller
 * declara `Pageable` como parámetro y en modo standalone no viene por defecto.
 *
 * Patrón portable ratificado en MEMORY.md (@WebMvcTest no existe en Boot 4.1).
 */
class ProductControllerTest {

    private MockMvc mockMvc;
    private ProductRepository repository;

    @BeforeEach
    void setUp() {
        repository = mock(ProductRepository.class);
        ProductController controller = new ProductController(repository);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    /**
     * GET /api/products?page=0&size=5 — 200 con array en $.content.
     */
    @Test
    void list_returnsPagedContent() throws Exception {
        Product p = new Product(1L, "Product-1", new BigDecimal("10.00"), "v1");
        Pageable pageable = PageRequest.of(0, 5);
        Page<Product> page = new PageImpl<>(List.of(p), pageable, 1);
        when(repository.findAll(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/products?page=0&size=5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Product-1"));
    }

    /**
     * GET /api/products/{id} — 200 + header ETag "v1".
     */
    @Test
    void get_returnsProductWithETag() throws Exception {
        Product p = new Product(1L, "Product-1", new BigDecimal("10.00"), "v1");
        when(repository.findById(eq(1L))).thenReturn(Optional.of(p));

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ETAG, "\"v1\""))
                .andExpect(jsonPath("$.name").value("Product-1"));
    }

    /**
     * GET /api/products/{id} con If-None-Match matching → 304 Not Modified,
     * sin cuerpo (el cliente ya tiene la copia).
     */
    @Test
    void get_returnsNotModifiedWhenETagMatches() throws Exception {
        Product p = new Product(1L, "Product-1", new BigDecimal("10.00"), "v1");
        when(repository.findById(eq(1L))).thenReturn(Optional.of(p));

        mockMvc.perform(get("/api/products/1").header(HttpHeaders.IF_NONE_MATCH, "\"v1\""))
                .andExpect(status().isNotModified());
    }

    /**
     * GET /api/products/{id} con X-API-Version=2 → JSON envuelto en `data`.
     */
    @Test
    void get_returnsWrappedBodyForApiVersion2() throws Exception {
        Product p = new Product(1L, "Product-1", new BigDecimal("10.00"), "v1");
        when(repository.findById(eq(1L))).thenReturn(Optional.of(p));

        mockMvc.perform(get("/api/products/1").header("X-API-Version", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Product-1"))
                .andExpect(jsonPath("$.data.id").value(1));
    }

    /**
     * GET /api/products/{id} inexistente → 404.
     */
    @Test
    void get_returnsNotFoundWhenMissing() throws Exception {
        when(repository.findById(eq(999L))).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/products/999"))
                .andExpect(status().isNotFound());
    }
}
