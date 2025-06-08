package com.mycompany.webapp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // すべてのパスに対して
                .allowedOrigins("https://yoka-ishikawa.github.io") // フロントのURLを許可
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 必要なHTTPメソッド
                .allowedHeaders("*") // 任意のヘッダーを許可
                .allowCredentials(false); // クッキーや認証情報を使わないなら false
    }
}
