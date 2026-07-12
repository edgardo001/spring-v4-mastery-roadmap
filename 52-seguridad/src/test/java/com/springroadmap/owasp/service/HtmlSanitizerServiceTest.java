package com.springroadmap.owasp.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class HtmlSanitizerServiceTest {

    private final HtmlSanitizerService service = new HtmlSanitizerService();

    @Test
    void removesScriptTag() {
        String dirty = "<script>alert(1)</script>Hola";
        String clean = service.sanitize(dirty);
        assertThat(clean).isEqualTo("Hola");
        assertThat(clean).doesNotContain("script");
    }

    @Test
    void keepsAllowedTags() {
        String dirty = "<p>Texto <b>importante</b></p><script>evil()</script>";
        String clean = service.sanitize(dirty);
        assertThat(clean).contains("<p>");
        assertThat(clean).contains("<b>importante</b>");
        assertThat(clean).doesNotContain("script");
    }

    @Test
    void handlesNull() {
        assertThat(service.sanitize(null)).isEqualTo("");
    }
}
