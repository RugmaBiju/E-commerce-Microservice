package com.example.razorpay.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web configuration for static resources, CORS, and other MVC settings.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Configure static resource handlers for CSS, JS, images, etc.
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // CSS files
        registry.addResourceHandler("/css/**")
                .addResourceLocations("classpath:/static/css/");

        // JavaScript files
        registry.addResourceHandler("/js/**")
                .addResourceLocations("classpath:/static/js/");

        // Images
        registry.addResourceHandler("/images/**")
                .addResourceLocations("classpath:/static/images/");

        // Fonts
        registry.addResourceHandler("/fonts/**")
                .addResourceLocations("classpath:/static/fonts/");
    }

    /**
     * Configure CORS (Cross-Origin Resource Sharing)
     * This allows the frontend to make requests from different origins if needed
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .maxAge(3600);
    }
}
