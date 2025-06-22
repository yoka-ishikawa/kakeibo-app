package com.mycompany.webapp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry
        .addMapping("/**") // すべてのパスに対して
        .allowedOrigins("https://kakeibo-app-gy0m.onrender.com", "http://localhost:8080")
        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 必要なHTTPメソッド
        .allowedHeaders("*") // 任意のヘッダーを許可
        .allowCredentials(true); // クッキーや認証情報を使用
  }
}
