package com.springroadmap.owasp.security;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private static final int MAX_REQUESTS_PER_WINDOW = 100;
    private static final long WINDOW_MILLIS = 60_000L;

    private final ConcurrentHashMap<String, Window> counters = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        String ip = request.getRemoteAddr() == null ? "unknown" : request.getRemoteAddr();
        long now = System.currentTimeMillis();

        Window w = counters.compute(ip, (k, existing) -> {
            if (existing == null || now - existing.start > WINDOW_MILLIS) {
                return new Window(now, new AtomicInteger(0));
            }
            return existing;
        });

        int count = w.count.incrementAndGet();
        if (count > MAX_REQUESTS_PER_WINDOW) {
            response.setStatus(429);
            response.getWriter().write("Too Many Requests");
            return;
        }
        filterChain.doFilter(request, response);
    }

    private record Window(long start, AtomicInteger count) {}
}
