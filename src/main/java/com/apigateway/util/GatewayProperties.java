package com.apigateway.util;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

@ConfigurationProperties(prefix = "gateway")
public class GatewayProperties {
    private Map<String, String> routes = new HashMap<>();

    public Map<String, String> getRoutes() { return routes; }
    public void setRoutes(Map<String, String> routes) { this.routes = routes; }
}