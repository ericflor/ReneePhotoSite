package com.MonopolySolutionsLLC.InventorySystem.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Inventory System API")
                        .version("v1")
                        .description("Phone Inventory System for Monopoly Solutions LLC"));
    }
}
