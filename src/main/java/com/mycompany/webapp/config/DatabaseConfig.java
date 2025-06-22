package com.mycompany.webapp.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * データベース設定（緊急対応版・SSL問題解決）
 * Render PostgreSQL接続エラー "SSL/TLS required" 対応
 */
@Configuration
@Profile("production")
public class DatabaseConfig {

    /**
     * 段階的接続試行によるデータソース作成
     * 1. SSL必須（証明書検証緩和）
     * 2. SSL優先（証明書検証なし）
     * 3. 最後の手段としてSSL無効
     */
    @Bean
    public DataSource dataSource() {
        System.out.println("=== データベース接続設定開始 ===");
        
        // 環境変数から接続情報を取得
        String databaseUrl = System.getenv("DATABASE_URL");
        String username = System.getenv("DB_USERNAME");
        String password = System.getenv("DB_PASSWORD");

        System.out.println("環境変数確認:");
        System.out.println("  DATABASE_URL: " + databaseUrl);
        System.out.println("  DB_USERNAME: " + username);
        System.out.println("  DB_PASSWORD: " + (password != null ? "設定済み" : "未設定"));

        // フォールバック設定（環境変数が未設定の場合）
        if (databaseUrl == null) {
            databaseUrl = "jdbc:postgresql://dpg-d12hm7je5dus7396aujg-a.singapore-postgres.render.com:5432/tb_info_kanri";
        }

        // ユーザー名のフォールバック: ログから確認された実際の値に合わせる
        if (username == null) {
            username = "kakeibo_user"; // ログから確認された実際の値
        }

        // 段階的接続試行: 1. SSL必須（証明書検証緩和）
        DataSource dataSource = trySSLConnection(databaseUrl, username, password, "require");
        if (dataSource != null) return dataSource;

        // 段階的接続試行: 2. SSL優先（証明書検証なし）
        dataSource = trySSLConnection(databaseUrl, username, password, "prefer");
        if (dataSource != null) return dataSource;

        // 段階的接続試行: 3. SSL無効（最後の手段）
        System.out.println("⚠️ 警告: SSL無効で接続を試行します（セキュリティリスクあり）");
        dataSource = trySSLConnection(databaseUrl, username, password, "disable");
        if (dataSource != null) return dataSource;

        throw new RuntimeException("すべての接続試行が失敗しました。データベースサーバーの状態を確認してください。");
    }

    /**
     * SSL設定を指定して接続を試行
     */
    private DataSource trySSLConnection(String baseUrl, String username, String password, String sslMode) {
        try {
            System.out.println("=== 接続試行: SSL " + sslMode + " ===");
            
            HikariConfig config = new HikariConfig();
            
            // URL構築（既存のパラメータを削除）
            String url = baseUrl;
            if (url.contains("?")) {
                url = url.substring(0, url.indexOf("?"));
            }
            
            // SSL設定に応じてパラメータを追加
            switch (sslMode) {
                case "require":
                    // SSL必須だが証明書検証を緩和
                    url += "?sslmode=require&sslcert=&sslkey=&sslrootcert=" +
                           "&connectTimeout=60&socketTimeout=60&tcpKeepAlive=true" +
                           "&ApplicationName=kakeibo-app&assumeMinServerVersion=9.0";
                    break;
                case "prefer":
                    // SSL優先（利用可能なら使用、不可能なら平文）
                    url += "?sslmode=prefer&connectTimeout=60&socketTimeout=60" +
                           "&tcpKeepAlive=true&ApplicationName=kakeibo-app" +
                           "&assumeMinServerVersion=9.0";
                    break;
                case "disable":
                    // SSL無効（最後の手段）
                    url += "?sslmode=disable&connectTimeout=60&socketTimeout=60" +
                           "&tcpKeepAlive=true&ApplicationName=kakeibo-app" +
                           "&assumeMinServerVersion=9.0";
                    break;
            }
            
            config.setJdbcUrl(url);
            config.setUsername(username);
            config.setPassword(password);
            config.setDriverClassName("org.postgresql.Driver");
            
            // 保守的な接続プール設定
            config.setMaximumPoolSize(2);
            config.setMinimumIdle(1);
            config.setConnectionTimeout(45000); // 45秒
            config.setIdleTimeout(300000); // 5分
            config.setMaxLifetime(600000); // 10分
            config.setValidationTimeout(15000); // 15秒
            config.setInitializationFailTimeout(45000); // 45秒
            config.setConnectionTestQuery("SELECT 1");
            
            // データソースプロパティ設定
            Properties props = new Properties();
            props.setProperty("connectTimeout", "60");
            props.setProperty("socketTimeout", "60");
            props.setProperty("tcpKeepAlive", "true");
            props.setProperty("ApplicationName", "kakeibo-app");
            props.setProperty("assumeMinServerVersion", "9.0");
            
            if ("require".equals(sslMode)) {
                props.setProperty("sslmode", "require");
                props.setProperty("sslcert", "");
                props.setProperty("sslkey", "");
                props.setProperty("sslrootcert", "");
            } else if ("prefer".equals(sslMode)) {
                props.setProperty("sslmode", "prefer");
            } else {
                props.setProperty("sslmode", "disable");
            }
            config.setDataSourceProperties(props);
            
            System.out.println("接続URL: " + url);
            System.out.println("接続試行中...");
            
            HikariDataSource dataSource = new HikariDataSource(config);
            
            // 接続テスト
            try (var conn = dataSource.getConnection()) {
                System.out.println("✅ 接続成功: SSL " + sslMode);
                System.out.println("PostgreSQL バージョン: " + conn.getMetaData().getDatabaseProductVersion());
                System.out.println("接続情報: " + conn.getMetaData().getURL());
                return dataSource;
            }
            
        } catch (Exception e) {
            System.err.println("❌ 接続失敗 (SSL " + sslMode + "): " + e.getMessage());
            if (e.getCause() != null) {
                System.err.println("根本原因: " + e.getCause().getClass().getSimpleName() + " - " + e.getCause().getMessage());
            }
            return null;
        }
    }
}
