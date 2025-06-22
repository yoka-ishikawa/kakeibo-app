package com.mycompany.webapp.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * データベース設定（緊急対応版）
 * Render PostgreSQL接続問題の解決を図る
 */
@Configuration
@Profile("production")
public class DatabaseConfig {

    /**
     * 緊急対応版データソース
     * 複数の接続オプションを試行して最適な設定を見つける
     */
    @Bean
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        
        // 環境変数から接続情報を取得
        String databaseUrl = System.getenv("DATABASE_URL");
        String username = System.getenv("DB_USERNAME");
        String password = System.getenv("DB_PASSWORD");
        
        // フォールバック設定（環境変数が未設定の場合）
        if (databaseUrl == null) {
            databaseUrl = "jdbc:postgresql://dpg-d12hm7je5dus7396aujg-a.singapore-postgres.render.com:5432/tb_info_kanri";
        }
        
        if (username == null) {
            username = "tb_info_kanri_user";
        }
        
        // SSL問題対応のため、複数の接続オプションを設定
        String enhancedUrl = databaseUrl;
        if (!enhancedUrl.contains("?")) {
            enhancedUrl += "?";
        } else {
            enhancedUrl += "&";
        }
        
        enhancedUrl += "sslmode=require" +
                      "&connectTimeout=60" +
                      "&socketTimeout=60" +
                      "&tcpKeepAlive=true" +
                      "&ApplicationName=kakeibo-app" +
                      "&loginTimeout=60" +
                      "&cancelSignalTimeout=30";
        
        config.setJdbcUrl(enhancedUrl);
        config.setUsername(username);
        config.setPassword(password);
        config.setDriverClassName("org.postgresql.Driver");
        
        // 接続プール設定（保守的な設定）
        config.setMaximumPoolSize(3);
        config.setMinimumIdle(1);
        config.setConnectionTimeout(180000); // 3分
        config.setIdleTimeout(300000); // 5分
        config.setMaxLifetime(900000); // 15分
        config.setValidationTimeout(60000); // 1分
        config.setLeakDetectionThreshold(180000); // 3分
        config.setInitializationFailTimeout(30000); // 30秒
        
        // 接続テスト設定
        config.setConnectionTestQuery("SELECT 1");
        
        // 詳細な接続プロパティ設定
        Properties props = new Properties();
        props.setProperty("sslmode", "require");
        props.setProperty("connectTimeout", "60");
        props.setProperty("socketTimeout", "60");
        props.setProperty("tcpKeepAlive", "true");
        props.setProperty("ApplicationName", "kakeibo-app");
        props.setProperty("loginTimeout", "60");
        props.setProperty("cancelSignalTimeout", "30");
        config.setDataSourceProperties(props);
        
        // ログ出力
        System.out.println("=== データベース接続設定（緊急対応版） ===");
        System.out.println("URL: " + enhancedUrl);
        System.out.println("Username: " + username);
        System.out.println("Password: " + (password != null ? "設定済み" : "未設定"));
        System.out.println("MaxPoolSize: " + config.getMaximumPoolSize());
        System.out.println("======================================");
        
        try {
            HikariDataSource dataSource = new HikariDataSource(config);
            
            // 接続テスト
            try (var conn = dataSource.getConnection()) {
                System.out.println("✅ データベース接続成功！");
                System.out.println("PostgreSQL バージョン: " + conn.getMetaData().getDatabaseProductVersion());
            }
            
            return dataSource;
        } catch (Exception e) {
            System.err.println("❌ データベース接続失敗: " + e.getMessage());
            e.printStackTrace();
            
            // 緊急フォールバック: SSL無効で再試行
            System.out.println("緊急フォールバック: SSL無効で再試行中...");
            return createFallbackDataSource(databaseUrl, username, password);
        }
    }
    
    /**
     * SSL無効のフォールバック用データソース
     */
    private DataSource createFallbackDataSource(String baseUrl, String username, String password) {
        try {
            HikariConfig config = new HikariConfig();
            
            // SSL無効の設定
            String fallbackUrl = baseUrl;
            if (fallbackUrl.contains("?")) {
                fallbackUrl = fallbackUrl.substring(0, fallbackUrl.indexOf("?"));
            }
            fallbackUrl += "?sslmode=disable&connectTimeout=60&socketTimeout=60";
            
            config.setJdbcUrl(fallbackUrl);
            config.setUsername(username);
            config.setPassword(password);
            config.setDriverClassName("org.postgresql.Driver");
            
            // 基本的な接続プール設定
            config.setMaximumPoolSize(2);
            config.setMinimumIdle(1);
            config.setConnectionTimeout(120000);
            config.setConnectionTestQuery("SELECT 1");
            
            System.out.println("フォールバックURL: " + fallbackUrl);
            
            HikariDataSource dataSource = new HikariDataSource(config);
            
            // 接続テスト
            try (var conn = dataSource.getConnection()) {
                System.out.println("✅ フォールバック接続成功（SSL無効）");
            }
            
            return dataSource;
        } catch (Exception e) {
            System.err.println("❌ フォールバック接続も失敗: " + e.getMessage());
            throw new RuntimeException("データベース接続に完全に失敗しました", e);
        }
    }
}
