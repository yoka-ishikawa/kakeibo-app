package com.mycompany.webapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WebappApplication {
  public static void main(String[] args) {
    // 緊急対応: 環境変数とシステム情報を詳細出力
    System.out.println("=== 家計簿アプリケーション起動 ===");
    System.out.println("環境変数詳細:"); // DATABASE_URLの詳細分析
    String databaseUrl = System.getenv("DATABASE_URL");
    System.out.println("  DATABASE_URL: " + (databaseUrl != null ? "設定済み" : "未設定"));
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
    try {
      SpringApplication.run(WebappApplication.class, args);
      System.out.println("✅ アプリケーション起動成功");
    } catch (Exception e) {
      System.err.println("❌ アプリケーション起動失敗");
      System.err.println("エラータイプ: " + e.getClass().getSimpleName());
      System.err.println("エラーメッセージ: " + e.getMessage());

      // プロダクション環境でのみデプロイ失敗通知を準備
      String activeProfile = System.getenv("SPRING_PROFILES_ACTIVE");
      if ("production".equals(activeProfile)) {
        System.err.println("=== デプロイ失敗通知準備 ===");
        System.err.println("サービス名: " + System.getenv("RENDER_SERVICE_NAME"));
        System.err.println("エラー詳細: " + e.getMessage());
        System.err.println("スタックトレースの先頭5行:");

        StackTraceElement[] stackTrace = e.getStackTrace();
        for (int i = 0; i < Math.min(5, stackTrace.length); i++) {
          System.err.println("  " + stackTrace[i]);
        }

        System.err.println("========================");
        System.err.println("🔔 この情報をもとに、外部からデプロイ失敗通知を送信してください");
        System.err.println("エンドポイント: POST /api/deploy/failure-with-log");
      }

      // エラーを再スローして正常にアプリケーションを終了
      throw new RuntimeException("アプリケーション起動に失敗しました", e);
    }
  }
}
