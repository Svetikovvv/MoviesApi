package com.example.moviesapi.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Обслуживание статических медиафайлов по маршруту /api/media/stream/**
//        registry.addResourceHandler("/api/media/stream/**")
//                .addResourceLocations("file:D:/Apache24/Apache24/htdocs/movies/")
//                .setCachePeriod(3600) // Настройка кэширования (по желанию)
//                .resourceChain(true);
    }
}
