package com.mycompany.webapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WebappApplication {
  public static void main(String[] args) {
    // 緊急対応: 環境変数とシステム情報を詳細出力
    System.out.println("=== 家計簿アプリケーション起動 ===");
    System.out.println("環境変数詳細:");
    
    // DATABASE_URLの詳細分析
    String databaseUrl = System.getenv("DATABASE_URL");
    System.out.println("  DATABASE_URL: " + databaseUrl);
    if (databaseUrl != null) {
      if (databaseUrl.contains("sslmode")) {
        System.out.println("    SSL設定検出: " + databaseUrl.substring(databaseUrl.indexOf("sslmode")));
      }
      if (databaseUrl.contains("singapore-postgres")) {
        System.out.println("    Render PostgreSQL確認: ✓");
      }
    }
    
    System.out.println("  DB_USERNAME: " + System.getenv("DB_USERNAME"));
    System.out.println("  DB_PASSWORD: " + (System.getenv("DB_PASSWORD") != null ? "設定済み" : "未設定"));
    System.out.println("  SPRING_PROFILES_ACTIVE: " + System.getenv("SPRING_PROFILES_ACTIVE"));
    
    // Render固有の環境変数確認
    System.out.println("Render環境確認:");
    System.out.println("  RENDER: " + System.getenv("RENDER"));
    System.out.println("  RENDER_SERVICE_NAME: " + System.getenv("RENDER_SERVICE_NAME"));
    
    System.out.println("システム情報:");
    System.out.println("  Java Version: " + System.getProperty("java.version"));
    System.out.println("============================");
    
    SpringApplication.run(WebappApplication.class, args);
  }
}
