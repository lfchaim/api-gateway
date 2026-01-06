package com.apigateway.web;

import java.net.URI;

import org.springframework.stereotype.Component;

import com.apigateway.util.GatewayProperties;

@Component
public class DestinationResolver {

    private final GatewayProperties props;

    public DestinationResolver(GatewayProperties props) {
        this.props = props;
    }

    public URI resolveBaseUri(String service) {
        String base = props.getRoutes().get(service);
        if (base == null || base.isBlank()) {
            throw new IllegalArgumentException("Service não configurado: " + service);
        }
        return URI.create(base);
    }
}