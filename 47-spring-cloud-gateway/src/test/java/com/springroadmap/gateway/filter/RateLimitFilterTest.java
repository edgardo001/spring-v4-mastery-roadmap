package com.springroadmap.gateway.filter;

import com.springroadmap.gateway.config.RouteConfig;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * RateLimitFilterTest — test unitario puro (sin Spring context).
 *
 * Verifica:
 *   - Las primeras 10 requests desde la misma IP pasan (chain se invoca).
 *   - La request 11 se rechaza con HTTP 429.
 */
class RateLimitFilterTest {

    private RateLimitFilter filter;

    @BeforeEach
    void setUp() {
        RouteConfig config = new RouteConfig();
        // rate-limit por defecto = 10 rq/s (ver RouteConfig.RateLimit).
        filter = new RateLimitFilter(config);
    }

    @Test
    void firstTenRequestsPassEleventhIsRejected() throws Exception {
        FilterChain chain = mock(FilterChain.class);

        // 10 requests OK — misma IP.
        for (int i = 0; i < 10; i++) {
            MockHttpServletRequest req = new MockHttpServletRequest("GET", "/api/users");
            req.setRemoteAddr("192.168.1.100");
            MockHttpServletResponse res = new MockHttpServletResponse();
            filter.doFilter(req, res, chain);
            assertThat(res.getStatus())
                    .as("Request %d debe pasar", i + 1)
                    .isEqualTo(200);
        }

        // El chain debió invocarse exactamente 10 veces.
        verify(chain, times(10)).doFilter(org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any());

        // Request 11 — misma IP → 429.
        MockHttpServletRequest req11 = new MockHttpServletRequest("GET", "/api/users");
        req11.setRemoteAddr("192.168.1.100");
        MockHttpServletResponse res11 = new MockHttpServletResponse();
        filter.doFilter(req11, res11, chain);

        assertThat(res11.getStatus()).isEqualTo(429);
        assertThat(res11.getContentAsString()).contains("rate_limit_exceeded");

        // El chain sigue habiendo sido invocado 10 veces (no 11).
        verify(chain, times(10)).doFilter(org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any());
    }

    @Test
    void differentIpsHaveIndependentBuckets() throws Exception {
        FilterChain chain = mock(FilterChain.class);

        // Consumimos los 10 tokens de la IP A.
        for (int i = 0; i < 10; i++) {
            MockHttpServletRequest req = new MockHttpServletRequest("GET", "/x");
            req.setRemoteAddr("10.0.0.1");
            filter.doFilter(req, new MockHttpServletResponse(), chain);
        }

        // La IP B tiene su propio bucket → debe pasar.
        MockHttpServletRequest reqB = new MockHttpServletRequest("GET", "/x");
        reqB.setRemoteAddr("10.0.0.2");
        MockHttpServletResponse resB = new MockHttpServletResponse();
        filter.doFilter(reqB, resB, chain);

        assertThat(resB.getStatus()).isEqualTo(200);
    }
}
