package com.springroadmap.jpaadv.controller;

import com.springroadmap.jpaadv.domain.Category;
import com.springroadmap.jpaadv.domain.Product;
import com.springroadmap.jpaadv.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * MockMvc en modo "standalone": construye el controller a mano con un
 * repositorio mockeado. NO levanta el contexto Spring — más rápido y
 * aislado que @WebMvcTest (que además ya no existe en Spring Boot 4.1.0).
 *
 * Patrón portable ratificado en MEMORY.md.
 */
class ProductControllerTest {

    private MockMvc mockMvc;
    private ProductRepository repository;

    @BeforeEach
    void setUp() {
        repository = mock(ProductRepository.class);
        ProductController controller = new ProductController(repository);
        // `standaloneSetup` = sin Spring context; solo el controller + su HTTP layer.
        // Registramos manualmente el PageableHandlerMethodArgumentResolver porque
        // el controller usa Pageable y en modo standalone no viene por defecto.
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    /**
     * GET /api/products/{id} — happy path. Retorna 200 + JSON con el producto.
     */
    @Test
    void get_returnsProductWhenFound() throws Exception {
        Category cat = new Category("Electronica");
        Product p = new Product("Laptop Pro", new BigDecimal("1500.00"), cat);
        when(repository.findWithCategoryById(eq(1L))).thenReturn(Optional.of(p));

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Laptop Pro"))
                .andExpect(jsonPath("$.price").value(1500.00));
    }

    /**
     * GET /api/products/{id} — 404 cuando no existe.
     */
    @Test
    void get_returnsNotFoundWhenMissing() throws Exception {
        when(repository.findWithCategoryById(eq(999L))).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/products/999"))
                .andExpect(status().isNotFound());
    }

    /**
     * GET /api/products — retorna un Page serializado a JSON con `content`.
     */
    @Test
    void list_returnsPageOfProducts() throws Exception {
        Product p = new Product("Laptop Pro", new BigDecimal("1500.00"), null);
        // Pasamos el Pageable explícitamente al PageImpl para evitar un warning
        // de Spring Data en Boot 4 (serialización de PageImpl "sin metadata" da 500).
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> page = new PageImpl<>(List.of(p), pageable, 1);
        when(repository.findByNameContaining(anyString(), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/products?q=lap&page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Laptop Pro"));
    }
}
