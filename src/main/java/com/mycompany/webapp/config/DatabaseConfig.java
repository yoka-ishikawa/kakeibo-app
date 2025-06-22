package com.mycompany.webapp.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.beans.factory.annotation.Autowired;
import com.mycompany.webapp.service.NotificationService;

import javax.sql.DataSource;

/**
 * データベース設定（改良版・詳細エラーハンドリング付き）
 * 環境変数の詳細チェックと段階的接続試行
 */
@Configuration
@Profile("production")
public class DatabaseConfig {
    
    @Autowired
    private NotificationService notificationService;

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
                    }                } else {
                    System.err.println("❌ 全ての接続試行が失敗しました");
                    
                    // 詳細なエラー情報をLINEに送信
                    try {
                        String errorDetails = "データベース接続に完全に失敗しました。" +
                                            "DATABASE_URL: " + maskUrl(databaseUrl) + "\\n" +
                                            "環境変数設定状況を確認してください。";
                        notificationService.sendConnectionErrorDetails(
                            e.getClass().getSimpleName(),
                            e.getMessage(),
                            errorDetails
                        );
                    } catch (Exception lineError) {
                        System.err.println("⚠️ LINE通知送信失敗: " + lineError.getMessage());
                    }
                    
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
        
        // URL形式の詳細解析とログ出力
        analyzeAndLogDatabaseUrl(databaseUrl);
        
        HikariConfig config = new HikariConfig();
        
        // 基本接続設定
        config.setJdbcUrl(databaseUrl);
        config.setUsername(username);
        config.setPassword(password);
        config.setDriverClassName("org.postgresql.Driver");
        
        // 接続プール設定（接続問題診断用に調整）
        config.setMaximumPoolSize(1);           // 診断用に最小限に
        config.setMinimumIdle(1);
        config.setConnectionTimeout(15000);     // 15秒（短縮）
        config.setValidationTimeout(5000);      // 5秒
        config.setIdleTimeout(300000);          // 5分
        config.setMaxLifetime(1800000);         // 30分
        config.setInitializationFailTimeout(30000); // 30秒（短縮）
        
        // 接続検証設定
        config.setConnectionTestQuery("SELECT 1");
        
        // PostgreSQL固有の設定（接続問題診断用）
        config.addDataSourceProperty("connectTimeout", "10");
        config.addDataSourceProperty("socketTimeout", "10");
        config.addDataSourceProperty("loginTimeout", "10");
        config.addDataSourceProperty("ssl", "true");
        config.addDataSourceProperty("sslmode", "require");
          // 直接JDBC接続テストを先に実行
        System.out.println("=== 1. 直接JDBC接続テスト ===");
        testDirectJdbcConnection(databaseUrl, username, password);
        
        System.out.println("=== 2. HikariDataSource作成 ===");
        HikariDataSource dataSource = new HikariDataSource(config);
        
        // 詳細な接続テスト実行
        System.out.println("=== 3. HikariCP経由接続テスト ===");
        try (var conn = dataSource.getConnection()) {
            System.out.println("✅ データベース接続成功！");
            var metadata = conn.getMetaData();
            System.out.println("  製品名: " + metadata.getDatabaseProductName());
            System.out.println("  バージョン: " + metadata.getDatabaseProductVersion());
            System.out.println("  URL: " + maskUrl(metadata.getURL()));
            System.out.println("  ドライバー: " + metadata.getDriverName() + " " + metadata.getDriverVersion());
            
            // 接続テストクエリ実行
            try (var stmt = conn.createStatement(); 
                 var rs = stmt.executeQuery("SELECT current_database(), current_user, version()")) {
                if (rs.next()) {
                    System.out.println("  データベース名: " + rs.getString(1));
                    System.out.println("  接続ユーザー: " + rs.getString(2));
                    System.out.println("  サーバー情報: " + rs.getString(3));
                }
            }
            
            return dataSource;
        } catch (Exception e) {
            System.err.println("❌ 接続テスト失敗:");
            System.err.println("  エラータイプ: " + e.getClass().getSimpleName());
            System.err.println("  エラーメッセージ: " + e.getMessage());
            if (e.getCause() != null) {
                System.err.println("  原因: " + e.getCause().getMessage());
            }
            dataSource.close();
            throw e;
        }
    }
    
    /**
     * 直接JDBC接続テスト（HikariCPを使わない詳細診断）
     */
    private void testDirectJdbcConnection(String url, String username, String password) {
        System.out.println("直接JDBC接続を試行します...");
        
        try {
            // PostgreSQLドライバーの明示的ロード
            Class.forName("org.postgresql.Driver");
            System.out.println("✅ PostgreSQLドライバー読み込み完了");
            
            // 接続プロパティ設定（タイムアウト短縮）
            java.util.Properties props = new java.util.Properties();
            props.setProperty("user", username);
            props.setProperty("password", password);
            props.setProperty("connectTimeout", "10");  // 10秒
            props.setProperty("socketTimeout", "20");   // 20秒
            props.setProperty("loginTimeout", "10");    // 10秒
            props.setProperty("ssl", "true");
            props.setProperty("sslmode", "require");
            props.setProperty("ApplicationName", "KakeiboApp-DirectTest");
            
            System.out.println("直接接続試行中（タイムアウト: 10秒）...");
            long startTime = System.currentTimeMillis();
            
            try (var conn = java.sql.DriverManager.getConnection(url, props)) {
                long connectionTime = System.currentTimeMillis() - startTime;
                System.out.println("✅ 直接JDBC接続成功！（" + connectionTime + "ms）");
                
                // 詳細なサーバー情報取得
                try (var stmt = conn.createStatement()) {
                    System.out.println("=== サーバー情報 ===");
                    try (var rs = stmt.executeQuery("SELECT version(), current_database(), current_user, inet_server_addr(), inet_server_port(), now()")) {
                        if (rs.next()) {
                            System.out.println("PostgreSQLバージョン: " + rs.getString(1));
                            System.out.println("データベース名: " + rs.getString(2));
                            System.out.println("接続ユーザー: " + rs.getString(3));
                            System.out.println("サーバーIP: " + rs.getString(4));
                            System.out.println("サーバーポート: " + rs.getString(5));
                            System.out.println("サーバー時刻: " + rs.getString(6));
                        }
                    }
                    
                    // 接続設定確認
                    System.out.println("=== 接続設定 ===");
                    try (var rs = stmt.executeQuery("SHOW ssl, SHOW port, SHOW max_connections")) {
                        while (rs.next()) {
                            System.out.println("設定値: " + rs.getString(1));
                        }
                    } catch (Exception e) {
                        System.out.println("設定確認をスキップ: " + e.getMessage());
                    }
                }
                
                System.out.println("✅ 直接JDBC接続テスト完了");
                
            } catch (java.sql.SQLException e) {
                System.err.println("❌ 直接JDBC接続失敗:");
                System.err.println("  SQLState: " + e.getSQLState());
                System.err.println("  ErrorCode: " + e.getErrorCode());
                System.err.println("  Message: " + e.getMessage());
                  // 具体的なエラー分析
                analyzeConnectionError(e);
                
                // 詳細なエラー情報をLINEに送信
                try {
                    String diagnostic = "直接JDBC接続が失敗しました。\\n" +
                                      "SQLState: " + e.getSQLState() + "\\n" +
                                      "ErrorCode: " + e.getErrorCode() + "\\n" +
                                      "データベースサーバーまたは接続設定を確認してください。";
                    notificationService.sendConnectionErrorDetails(
                        "SQLException",
                        e.getMessage(),
                        diagnostic
                    );
                } catch (Exception lineError) {
                    System.err.println("⚠️ LINE通知送信失敗: " + lineError.getMessage());
                }
                
                throw new RuntimeException("直接JDBC接続に失敗", e);
            }
            
        } catch (ClassNotFoundException e) {
            System.err.println("❌ PostgreSQLドライバーが見つかりません: " + e.getMessage());
            throw new RuntimeException("PostgreSQLドライバーロードエラー", e);
        } catch (Exception e) {
            System.err.println("❌ 直接JDBC接続で予期しないエラー: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            if (e.getCause() != null) {
                System.err.println("根本原因: " + e.getCause().getMessage());
            }
            throw new RuntimeException("直接JDBC接続テスト失敗", e);
        }
    }
    
    /**
     * 接続エラーの詳細分析
     */
    private void analyzeConnectionError(java.sql.SQLException e) {
        String message = e.getMessage();
        String sqlState = e.getSQLState();
        
        System.err.println("=== エラー分析 ===");
        
        if (message != null) {
            if (message.contains("Connection refused") || message.contains("could not connect")) {
                System.err.println("💡 原因: データベースサーバーが起動していないか、ポートが閉じています");
                System.err.println("💡 対処: RenderダッシュボードでPostgreSQLサービスの状態を確認してください");
            } else if (message.contains("timeout") || message.contains("timed out")) {
                System.err.println("💡 原因: ネットワーク接続が遅いか、サーバーが応答していません");
                System.err.println("💡 対処: Internal Database URLを使用し、タイムアウト設定を見直してください");
            } else if (message.contains("authentication failed") || message.contains("password")) {
                System.err.println("💡 原因: 認証情報（ユーザー名/パスワード）が間違っています");
                System.err.println("💡 対処: 環境変数DB_USERNAME, DB_PASSWORDを確認してください");
            } else if (message.contains("database") && message.contains("does not exist")) {
                System.err.println("💡 原因: 指定されたデータベースが存在しません");
                System.err.println("💡 対処: DATABASE_URLのデータベース名を確認してください");
            } else if (message.contains("host") || message.contains("unknown host")) {
                System.err.println("💡 原因: ホスト名が解決できません");
                System.err.println("💡 対処: Internal Database URLを使用してください");
            } else if (message.contains("SSL")) {
                System.err.println("💡 原因: SSL接続の問題です");
                System.err.println("💡 対処: SSL設定を確認してください");
            }
        }
        
        if (sqlState != null) {
            System.err.println("SQLState: " + sqlState);
            switch (sqlState) {
                case "08001":
                    System.err.println("💡 SQLState分析: サーバーに接続できません");
                    break;
                case "08004":
                    System.err.println("💡 SQLState分析: サーバーが接続を拒否しました");
                    break;
                case "28000":
                    System.err.println("💡 SQLState分析: 認証に失敗しました");
                    break;
                case "3D000":
                    System.err.println("💡 SQLState分析: データベースが存在しません");
                    break;
                default:
                    System.err.println("💡 SQLState分析: 不明なエラー状態");
            }
        }
    }
    
    /**
     * データベースURL解析と診断情報出力
     */
    private void analyzeAndLogDatabaseUrl(String databaseUrl) {
        System.out.println("データベースURL解析:");
        System.out.println("  完全URL: " + maskUrl(databaseUrl));
        
        try {
            // URLパターン解析
            if (databaseUrl.startsWith("postgres://")) {
                System.out.println("  プロトコル: PostgreSQL (postgres://)");
            } else if (databaseUrl.startsWith("postgresql://")) {
                System.out.println("  プロトコル: PostgreSQL (postgresql://)");
            } else {
                System.out.println("  ❌ 未対応プロトコル: " + databaseUrl.substring(0, databaseUrl.indexOf("://")));
            }
            
            // ホスト・ポート抽出
            String hostPart = databaseUrl.substring(databaseUrl.indexOf("://") + 3);
            if (hostPart.contains("@")) {
                hostPart = hostPart.substring(hostPart.indexOf("@") + 1);
            }
            
            String host = hostPart.split("/")[0];
            if (host.contains(":")) {
                String[] hostPort = host.split(":");
                System.out.println("  ホスト: " + hostPort[0]);
                System.out.println("  ポート: " + hostPort[1]);
                
                // Internal URLかExternal URLかの判定
                if (hostPort[0].contains("internal") || hostPort[0].endsWith(".internal")) {
                    System.out.println("  ✅ Internal URL を使用 (推奨)");
                } else if (hostPort[0].contains("render.com") || hostPort[0].contains("oregon-postgres")) {
                    System.out.println("  ⚠️  External URL を使用 (Internal URL推奨)");
                } else {
                    System.out.println("  ❓ URL形式を確認してください");
                }
            } else {
                System.out.println("  ホスト: " + host + " (デフォルトポート)");
            }
            
            // データベース名抽出
            if (hostPart.contains("/")) {
                String dbName = hostPart.substring(hostPart.indexOf("/") + 1);
                if (dbName.contains("?")) {
                    dbName = dbName.substring(0, dbName.indexOf("?"));
                }
                System.out.println("  データベース名: " + dbName);
            }
            
        } catch (Exception e) {
            System.err.println("  ❌ URL解析エラー: " + e.getMessage());
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
