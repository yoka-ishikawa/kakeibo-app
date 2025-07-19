package com.mycompany.webapp.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;

/**
 * Supabaseデータベース接続設定
 * 
 * 環境変数から取得したDATABASE_URLを適切なJDBC形式に変換し、
 * HikariCPデータソースを構築する。
 */
@Configuration
@Profile("production-supabase")
public class SupabaseDataSourceConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(SupabaseDataSourceConfig.class);
    
    private final Environment env;
    
    public SupabaseDataSourceConfig(Environment env) {
        this.env = env;
    }
    
    @Bean
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource.hikari")
    public DataSource dataSource() {
        String rawDatabaseUrl = env.getProperty("DATABASE_URL");
        String username = env.getProperty("DB_USERNAME");
        String password = env.getProperty("DB_PASSWORD");
        
        logger.info("=== Supabase DataSource設定デバッグ開始 ===");
        logger.info("DATABASE_URL環境変数: {}", maskPassword(rawDatabaseUrl));
        logger.info("DB_USERNAME環境変数: {}", username);
        logger.info("DB_PASSWORD設定: {}", password != null ? "[設定済み]" : "[未設定]");
        
        if (!StringUtils.hasText(rawDatabaseUrl)) {
            throw new IllegalStateException("DATABASE_URL環境変数が設定されていません");
        }
        
        if (!StringUtils.hasText(username)) {
            throw new IllegalStateException("DB_USERNAME環境変数が設定されていません");
        }
        
        if (!StringUtils.hasText(password)) {
            throw new IllegalStateException("DB_PASSWORD環境変数が設定されていません");
        }
        
        // PostgreSQL URLをJDBC形式に変換
        String jdbcUrl = convertToJdbcUrl(rawDatabaseUrl);
        
        logger.info("変換後JDBC URL: {}", maskPassword(jdbcUrl));
        
        // HikariCP設定
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);
        config.setDriverClassName("org.postgresql.Driver");
        
        // Supabase最適化設定
        config.setMaximumPoolSize(5);
        config.setMinimumIdle(2);
        config.setConnectionTimeout(60000);
        config.setIdleTimeout(300000);
        config.setMaxLifetime(1200000);
        config.setLeakDetectionThreshold(60000);
        
        // SSL設定
        config.addDataSourceProperty("sslmode", "require");
        config.addDataSourceProperty("serverTimezone", "Asia/Tokyo");
        
        try {
            HikariDataSource dataSource = new HikariDataSource(config);
            logger.info("Supabase HikariDataSource初期化成功");
            logger.info("接続プール最大サイズ: {}", config.getMaximumPoolSize());
            logger.info("=== Supabase DataSource設定デバッグ完了 ===");
            return dataSource;
        } catch (Exception e) {
            logger.error("Supabase DataSource初期化失敗", e);
            throw new RuntimeException("DataSource初期化に失敗しました: " + e.getMessage(), e);
        }
    }
    
    /**
     * PostgreSQL URLをJDBC形式に変換
     * 
     * @param postgresUrl postgresql://user:pass@host:port/database 形式のURL
     * @return jdbc:postgresql://host:port/database 形式のURL
     */
    private String convertToJdbcUrl(String postgresUrl) {
        logger.debug("URL変換処理開始: {}", maskPassword(postgresUrl));
        
        if (postgresUrl.startsWith("jdbc:")) {
            logger.debug("既にJDBC形式: そのまま使用");
            return postgresUrl;
        }
        
        if (postgresUrl.startsWith("postgresql://")) {
            // postgresql://user:pass@host:port/database -> jdbc:postgresql://host:port/database
            String withoutPrefix = postgresUrl.substring("postgresql://".length());
            
            // ユーザー認証情報を削除
            int atIndex = withoutPrefix.indexOf('@');
            if (atIndex > 0) {
                String hostPortDb = withoutPrefix.substring(atIndex + 1);
                String jdbcUrl = "jdbc:postgresql://" + hostPortDb;
                logger.debug("変換完了: {} -> {}", maskPassword(postgresUrl), maskPassword(jdbcUrl));
                return jdbcUrl;
            } else {
                // @がない場合はそのまま使用
                String jdbcUrl = "jdbc:postgresql://" + withoutPrefix;
                logger.debug("変換完了（認証情報なし）: {} -> {}", postgresUrl, jdbcUrl);
                return jdbcUrl;
            }
        }
        
        logger.warn("不明なURL形式: {}", maskPassword(postgresUrl));
        throw new IllegalArgumentException("サポートされていないDATABASE_URL形式: " + maskPassword(postgresUrl));
    }
    
    /**
     * ログ出力用にパスワード部分をマスクする
     */
    private String maskPassword(String url) {
        if (url == null) return null;
        return url.replaceAll("://[^:]+:[^@]+@", "://***:***@");
    }
}
