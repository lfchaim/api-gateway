package com.apigateway.controller;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.HandlerMapping;

import com.apigateway.service.GatewayService;

import jakarta.servlet.http.HttpServletRequest;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/gateway")
public class GatewayController {

    private final GatewayService gatewayService;

    public GatewayController(GatewayService gatewayService) {
        this.gatewayService = gatewayService;
    }

    @GetMapping("/{service}/**")
    public Mono<ResponseEntity<byte[]>> get(@PathVariable String service, HttpServletRequest request) {
        return forward(service, HttpMethod.GET, request, null);
    }

    @PostMapping("/{service}/**")
    public Mono<ResponseEntity<byte[]>> post(@PathVariable String service,
                                             @RequestBody(required = false) byte[] body,
                                             HttpServletRequest request) {
        return forward(service, HttpMethod.POST, request, body);
    }

    @PutMapping("/{service}/**")
    public Mono<ResponseEntity<byte[]>> put(@PathVariable String service,
                                            @RequestBody(required = false) byte[] body,
                                            HttpServletRequest request) {
        return forward(service, HttpMethod.PUT, request, body);
    }

    @PatchMapping("/{service}/**")
    public Mono<ResponseEntity<byte[]>> patch(@PathVariable String service,
                                              @RequestBody(required = false) byte[] body,
                                              HttpServletRequest request) {
        return forward(service, HttpMethod.PATCH, request, body);
    }

    @DeleteMapping("/{service}/**")
    public Mono<ResponseEntity<byte[]>> delete(@PathVariable String service,
                                               @RequestBody(required = false) byte[] body,
                                               HttpServletRequest request) {
        return forward(service, HttpMethod.DELETE, request, body);
    }

    private Mono<ResponseEntity<byte[]>> forward(String service,
                                                 HttpMethod method,
                                                 HttpServletRequest request,
                                                 byte[] body) {
        String remainderPath = extractRemainderPath(request);
        return gatewayService.forward(service, remainderPath, method, request, body);
    }

    // Extrai o trecho depois de /{service}
    private String extractRemainderPath(HttpServletRequest request) {
        String pathWithinMapping = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        String bestMatchPattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String remainder = new AntPathMatcher().extractPathWithinPattern(bestMatchPattern, pathWithinMapping);

        // remainder vem tipo: "v1/items/123"
        return remainder == null || remainder.isBlank() ? "" : "/" + remainder;
    }
}
