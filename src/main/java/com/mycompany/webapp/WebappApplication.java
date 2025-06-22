package com.mycompany.webapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WebappApplication {  public static void main(String[] args) {
    // 緊急対応: 環境変数とシステム情報を詳細出力
    System.out.println("=== 家計簿アプリケーション起動 ===");
    System.out.println("環境情報:");
    System.out.println("  DATABASE_URL: " + System.getenv("DATABASE_URL"));
    System.out.println("  DB_USERNAME: " + System.getenv("DB_USERNAME"));
    System.out.println("  DB_PASSWORD: " + (System.getenv("DB_PASSWORD") != null ? "設定済み" : "未設定"));
    System.out.println("  SPRING_PROFILES_ACTIVE: " + System.getenv("SPRING_PROFILES_ACTIVE"));
    System.out.println("システム情報:");
    System.out.println("  Java Version: " + System.getProperty("java.version"));
    System.out.println("============================");
    
    SpringApplication.run(WebappApplication.class, args);
  }
}
