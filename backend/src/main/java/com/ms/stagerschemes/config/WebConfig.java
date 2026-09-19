package com.ms.stagerschemes.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

  private static final String VITE_DEV_SERVER_ORIGIN = "http://localhost:5173";

  @Override
  public void addCorsMappings(CorsRegistry corsRegistry) {
    corsRegistry
        .addMapping("/api/**")
        .allowedOrigins(VITE_DEV_SERVER_ORIGIN)
        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
        .allowCredentials(true);
  }

  @Override
  public void addViewControllers(ViewControllerRegistry viewControllerRegistry) {
    viewControllerRegistry.addViewController("/{path:[^\\.]*}").setViewName("forward:/index.html");
    viewControllerRegistry
        .addViewController("/{path:[^\\.]*}/{subpath:[^\\.]*}")
        .setViewName("forward:/index.html");
  }
}
