package com.springroadmap.owasp.service;

import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;
import org.springframework.stereotype.Service;

@Service
public class HtmlSanitizerService {

    private final PolicyFactory policy;

    public HtmlSanitizerService() {
        this.policy = new HtmlPolicyBuilder()
            .allowElements("b", "i", "em", "strong", "p", "br", "ul", "ol", "li", "a")
            .allowUrlProtocols("https")
            .allowAttributes("href").onElements("a")
            .toFactory();
    }

    public String sanitize(String rawHtml) {
        if (rawHtml == null) return "";
        return policy.sanitize(rawHtml);
    }
}
