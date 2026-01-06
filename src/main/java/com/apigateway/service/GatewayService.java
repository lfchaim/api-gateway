package com.apigateway.service;

import java.net.URI;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import com.apigateway.web.DestinationResolver;
import com.apigateway.web.HeaderSanitizer;

import jakarta.servlet.http.HttpServletRequest;
import reactor.core.publisher.Mono;

@Service
public class GatewayService {

    private final WebClient webClient;
    private final DestinationResolver resolver;
    private final HeaderSanitizer headerSanitizer;

    public GatewayService(WebClient.Builder builder,
                          DestinationResolver resolver,
                          HeaderSanitizer headerSanitizer) {
        this.webClient = builder.build();
        this.resolver = resolver;
        this.headerSanitizer = headerSanitizer;
    }

    public Mono<ResponseEntity<byte[]>> forward(
            String service,
            String remainderPath,
            HttpMethod method,
            HttpServletRequest request,
            byte[] bodyOrNull
    ) {
        URI baseUri = resolver.resolveBaseUri(service);

        // Querystring original
        String query = request.getQueryString();

        URI targetUri = UriComponentsBuilder.fromUri(baseUri)
                .path(remainderPath == null ? "" : remainderPath)
                .replaceQuery(query)
                .build(true)
                .toUri();

        HttpHeaders incomingHeaders = new ServletServerHttpRequest(request).getHeaders();
        HttpHeaders outboundHeaders = headerSanitizer.sanitizeRequestHeaders(incomingHeaders);

        WebClient.RequestBodySpec spec = webClient
                .method(method)
                .uri(targetUri)
                .headers(h -> h.addAll(outboundHeaders));

        // Body: só envia quando fizer sentido (POST/PUT/PATCH, etc.)
        WebClient.RequestHeadersSpec<?> headersSpec =
                (bodyOrNull != null && bodyOrNull.length > 0 && allowsRequestBody(method))
                        ? spec.body(BodyInserters.fromValue(bodyOrNull))
                        : spec;

        return headersSpec.exchangeToMono(resp ->
                resp.bodyToMono(byte[].class)
                        .defaultIfEmpty(new byte[0])
                        .map(bytes -> {
                            HttpHeaders sanitizedRespHeaders =
                                    headerSanitizer.sanitizeResponseHeaders(resp.headers().asHttpHeaders());

                            return ResponseEntity
                                    .status(resp.statusCode())
                                    .headers(sanitizedRespHeaders)
                                    .body(bytes);
                        })
        );
    }

    private boolean allowsRequestBody(HttpMethod method) {
        return HttpMethod.POST.equals(method)
                || HttpMethod.PUT.equals(method)
                || HttpMethod.PATCH.equals(method)
                || HttpMethod.DELETE.equals(method); // às vezes DELETE tem body (não comum, mas existe)
    }
}