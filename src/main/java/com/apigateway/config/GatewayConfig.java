package com.apigateway.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import com.apigateway.util.GatewayProperties;

@Configuration
@EnableConfigurationProperties(GatewayProperties.class)  // ← Isso registra o bean
public class GatewayConfig {

    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }
}