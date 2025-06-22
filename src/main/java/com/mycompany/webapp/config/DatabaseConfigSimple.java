package com.mycompany.webapp.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

/**
 * データベース設定（緊急対応・シンプル版）
 * 複雑な段階的試行を一旦停止し、最もシンプルな設定でテスト
 */
@Configuration
@Profile("production")
public class DatabaseConfig {

    @Bean
    public DataSource dataSource() {
        System.out.println("=== シンプルデータベース接続設定 ===");
        
        // 環境変数から接続情報を取得
        String databaseUrl = System.getenv("DATABASE_URL");
        String username = System.getenv("DB_USERNAME");
        String password = System.getenv("DB_PASSWORD");

        System.out.println("接続情報確認:");
        System.out.println("  URL: " + databaseUrl);
        System.out.println("  User: " + username);
        System.out.println("  Pass: " + (password != null ? "設定済み" : "未設定"));
        
        // 環境変数が不正な場合の対応
        if (databaseUrl == null || username == null || password == null) {
            String error = "❌ 環境変数が不完全です:\n" +
                          "  DATABASE_URL: " + (databaseUrl != null ? "設定済み" : "未設定") + "\n" +
                          "  DB_USERNAME: " + (username != null ? "設定済み" : "未設定") + "\n" +
                          "  DB_PASSWORD: " + (password != null ? "設定済み" : "未設定");
            System.err.println(error);
            throw new RuntimeException("データベース環境変数が設定されていません。Renderの設定を確認してください。");
        }

        try {
            HikariConfig config = new HikariConfig();
            
            // 最もシンプルな設定
            config.setJdbcUrl(databaseUrl);
            config.setUsername(username);
            config.setPassword(password);
            config.setDriverClassName("org.postgresql.Driver");
            
            // 最小限のプール設定
            config.setMaximumPoolSize(1);
            config.setMinimumIdle(1);
            config.setConnectionTimeout(30000); // 30秒
            config.setValidationTimeout(10000); // 10秒
            config.setInitializationFailTimeout(60000); // 1分
            
            System.out.println("データソース作成試行中...");
            HikariDataSource dataSource = new HikariDataSource(config);
            
            System.out.println("接続テスト実行中...");
            try (var conn = dataSource.getConnection()) {
                System.out.println("✅ データベース接続成功！");
                var metadata = conn.getMetaData();
                System.out.println("  製品名: " + metadata.getDatabaseProductName());
                System.out.println("  バージョン: " + metadata.getDatabaseProductVersion());
                System.out.println("  URL: " + metadata.getURL());
                return dataSource;
            }
            
        } catch (Exception e) {
            System.err.println("❌ データベース接続失敗:");
            System.err.println("  エラータイプ: " + e.getClass().getSimpleName());
            System.err.println("  エラーメッセージ: " + e.getMessage());
            
            if (e.getCause() != null) {
                System.err.println("  根本原因: " + e.getCause().getClass().getSimpleName());
                System.err.println("  根本原因メッセージ: " + e.getCause().getMessage());
            }
            
            // スタックトレースの最初の数行のみ表示
            StackTraceElement[] stackTrace = e.getStackTrace();
            System.err.println("  スタックトレース（抜粋）:");
            for (int i = 0; i < Math.min(5, stackTrace.length); i++) {
                System.err.println("    " + stackTrace[i]);
            }
            
            throw new RuntimeException("データベース接続に失敗しました。詳細は上記ログを確認してください。", e);
        }
    }
}
