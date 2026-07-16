package com.monji.chatapp.gatway_service.filter;

import org.springframework.cloud.gateway.server.mvc.filter.FilterSupplier;
import org.springframework.cloud.gateway.server.mvc.filter.SimpleFilterSupplier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatwayFilterConfig {

    @Bean
    public FilterSupplier gatewayFilterSupplier() {
        return new SimpleFilterSupplier(GatewayAuthFilter.class);
    }
}
