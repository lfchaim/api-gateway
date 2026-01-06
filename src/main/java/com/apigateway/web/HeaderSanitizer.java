package com.apigateway.web;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class HeaderSanitizer {

    // RFC 7230 - hop-by-hop headers (não devem ser repassados)
    private static final Set<String> HOP_BY_HOP = Set.of(
            "connection", "keep-alive", "proxy-authenticate", "proxy-authorization",
            "te", "trailer", "transfer-encoding", "upgrade",
            "host", "content-length"
    );

    public HttpHeaders sanitizeRequestHeaders(HttpHeaders incoming) {
        HttpHeaders out = new HttpHeaders();
        incoming.forEach((k, v) -> {
            if (!HOP_BY_HOP.contains(k.toLowerCase())) {
                out.put(k, v);
            }
        });
        return out;
    }

    public HttpHeaders sanitizeResponseHeaders(HttpHeaders incoming) {
        HttpHeaders out = new HttpHeaders();
        incoming.forEach((k, v) -> {
            if (!HOP_BY_HOP.contains(k.toLowerCase())) {
                out.put(k, v);
            }
        });
        return out;
    }
}