package com.mycompany.webapp.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

/**
 * データベース設定（改良版・詳細エラーハンドリング付き）
 * 環境変数の詳細チェックと段階的接続試行
 */
@Configuration
@Profile("production")
public class DatabaseConfig {

    @Bean
    public DataSource dataSource() {
        System.out.println("=== データベース接続設定開始 ===");
        
        // 環境変数の詳細確認
        String databaseUrl = System.getenv("DATABASE_URL");
        String username = System.getenv("DB_USERNAME");
        String password = System.getenv("DB_PASSWORD");
        String renderServiceName = System.getenv("RENDER_SERVICE_NAME");
        String springProfile = System.getenv("SPRING_PROFILES_ACTIVE");

        System.out.println("環境変数確認:");
        System.out.println("  DATABASE_URL: " + (databaseUrl != null ? maskUrl(databaseUrl) : "❌ 未設定"));
        System.out.println("  DB_USERNAME: " + (username != null ? "✅ 設定済み (" + username + ")" : "❌ 未設定"));
        System.out.println("  DB_PASSWORD: " + (password != null ? "✅ 設定済み (長さ: " + password.length() + ")" : "❌ 未設定"));
        System.out.println("  RENDER_SERVICE_NAME: " + renderServiceName);
        System.out.println("  SPRING_PROFILES_ACTIVE: " + springProfile);
        
        // 必須環境変数の検証
        if (databaseUrl == null || databaseUrl.trim().isEmpty()) {
            String errorMsg = "❌ DATABASE_URL が設定されていません。\n" +
                             "Renderダッシュボードで以下を確認してください:\n" +
                             "1. PostgreSQLサービスが作成されているか\n" +
                             "2. 環境変数 DATABASE_URL が設定されているか\n" +
                             "3. DATABASE_URL の形式: postgres://user:pass@host:port/db";
            System.err.println(errorMsg);
            throw new RuntimeException(errorMsg);
        }
        
        if (username == null || username.trim().isEmpty()) {
            String errorMsg = "❌ DB_USERNAME が設定されていません。\n" +
                             "Renderダッシュボードの環境変数で DB_USERNAME を設定してください。";
            System.err.println(errorMsg);
            throw new RuntimeException(errorMsg);
        }
        
        if (password == null || password.trim().isEmpty()) {
            String errorMsg = "❌ DB_PASSWORD が設定されていません。\n" +
                             "Renderダッシュボードの環境変数で DB_PASSWORD を設定してください。";
            System.err.println(errorMsg);
            throw new RuntimeException(errorMsg);
        }

        return createDataSourceWithRetry(databaseUrl, username, password);
    }
    
    /**
     * リトライ機能付きデータソース作成
     */
    private DataSource createDataSourceWithRetry(String databaseUrl, String username, String password) {
        int maxRetries = 3;
        long retryDelay = 2000; // 2秒
        
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                System.out.println("データベース接続試行 " + attempt + "/" + maxRetries);
                return createDataSource(databaseUrl, username, password);
            } catch (Exception e) {
                System.err.println("❌ 接続試行 " + attempt + " 失敗: " + e.getMessage());
                
                if (attempt < maxRetries) {
                    System.out.println("⏳ " + (retryDelay / 1000) + "秒後に再試行します...");
                    try {
                        Thread.sleep(retryDelay);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("データベース接続の再試行が中断されました", ie);
                    }
                } else {
                    System.err.println("❌ 全ての接続試行が失敗しました");
                    throw new RuntimeException("データベース接続に失敗しました（" + maxRetries + "回試行）", e);
                }
            }
        }
        
        throw new RuntimeException("予期しないエラー: データソース作成に失敗");
    }
    
    /**
     * データソース作成の実際の処理
     */
    private DataSource createDataSource(String databaseUrl, String username, String password) throws Exception {
        System.out.println("HikariCP設定作成中...");
        
        HikariConfig config = new HikariConfig();
        
        // 基本接続設定
        config.setJdbcUrl(databaseUrl);
        config.setUsername(username);
        config.setPassword(password);
        config.setDriverClassName("org.postgresql.Driver");
        
        // 接続プール設定（本番環境用に最適化）
        config.setMaximumPoolSize(2);  // 小規模アプリ用
        config.setMinimumIdle(1);
        config.setConnectionTimeout(30000);     // 30秒
        config.setValidationTimeout(5000);      // 5秒
        config.setIdleTimeout(300000);          // 5分
        config.setMaxLifetime(1800000);         // 30分
        config.setInitializationFailTimeout(60000); // 1分
        
        // 接続検証設定
        config.setConnectionTestQuery("SELECT 1");
        
        // PostgreSQL固有の設定
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        
        System.out.println("HikariDataSource作成中...");
        HikariDataSource dataSource = new HikariDataSource(config);
        
        // 接続テスト実行
        System.out.println("接続テスト実行中...");
        try (var conn = dataSource.getConnection()) {
            System.out.println("✅ データベース接続成功！");
            var metadata = conn.getMetaData();
            System.out.println("  製品名: " + metadata.getDatabaseProductName());
            System.out.println("  バージョン: " + metadata.getDatabaseProductVersion());
            System.out.println("  URL: " + maskUrl(metadata.getURL()));
            System.out.println("  ドライバー: " + metadata.getDriverName() + " " + metadata.getDriverVersion());
            return dataSource;
        }
    }
    
    /**
     * URLのパスワード部分をマスク
     */
    private String maskUrl(String url) {
        if (url == null) return null;
        return url.replaceAll("://[^:]+:[^@]+@", "://***:***@");
    }
}
