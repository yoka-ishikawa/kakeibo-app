package com.mycompany.webapp.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/** データベース接続テスト用ユーティリティクラス Render PostgreSQL接続問題の診断に使用 */
public class DatabaseConnectionChecker {

  public static void main(String[] args) {
    System.out.println("=== Render PostgreSQL接続診断ツール ===");

    // 基本接続テスト
    testBasicConnection();

    // SSL設定を変更したテスト
    testWithDifferentSSL();

    // タイムアウト設定を変更したテスト
    testWithTimeout();
  }

  /** 基本的な接続テスト */
  private static void testBasicConnection() {
    System.out.println("\n1. 基本接続テスト");
    String url =
        "jdbc:postgresql://dpg-d12hm7je5dus7396aujg-a.singapore-postgres.render.com:5432/tb_info_kanri?sslmode=require";
    String username = System.getenv("DB_USERNAME");
    String password = System.getenv("DB_PASSWORD");

    System.out.println("URL: " + url);
    System.out.println("Username: " + (username != null ? username : "環境変数未設定"));
    System.out.println("Password: " + (password != null ? "設定済み" : "環境変数未設定"));

    testConnection(url, username, password, "基本接続");
  }

  /** SSL設定を変更したテスト */
  private static void testWithDifferentSSL() {
    System.out.println("\n2. SSL設定変更テスト");
    String baseUrl =
        "jdbc:postgresql://dpg-d12hm7je5dus7396aujg-a.singapore-postgres.render.com:5432/tb_info_kanri";
    String username = System.getenv("DB_USERNAME");
    String password = System.getenv("DB_PASSWORD");

    // SSL無効
    testConnection(baseUrl + "?sslmode=disable", username, password, "SSL無効");

    // SSL優先（証明書検証なし）
    testConnection(baseUrl + "?sslmode=prefer", username, password, "SSL優先");

    // SSL必須（証明書検証なし）
    testConnection(
        baseUrl + "?sslmode=require&sslcert=&sslkey=&sslrootcert=",
        username,
        password,
        "SSL必須（証明書検証なし）");
  }

  /** タイムアウト設定付きテスト */
  private static void testWithTimeout() {
    System.out.println("\n3. タイムアウト設定テスト");
    String url =
        "jdbc:postgresql://dpg-d12hm7je5dus7396aujg-a.singapore-postgres.render.com:5432/tb_info_kanri?sslmode=require&connectTimeout=60&socketTimeout=60";
    String username = System.getenv("DB_USERNAME");
    String password = System.getenv("DB_PASSWORD");

    testConnection(url, username, password, "タイムアウト設定付き");
  }

  /** 実際の接続テストを実行 */
  private static void testConnection(
      String url, String username, String password, String testName) {
    System.out.println("\n--- " + testName + " ---");

    if (username == null || password == null) {
      System.out.println("❌ エラー: 環境変数DB_USERNAME, DB_PASSWORDが設定されていません");
      return;
    }

    try {
      // PostgreSQLドライバーをロード
      Class.forName("org.postgresql.Driver");

      Properties props = new Properties();
      props.setProperty("user", username);
      props.setProperty("password", password);
      props.setProperty("loginTimeout", "60");
      props.setProperty("socketTimeout", "60");

      System.out.println("接続試行中...");
      long startTime = System.currentTimeMillis();

      try (Connection conn = DriverManager.getConnection(url, props)) {
        long endTime = System.currentTimeMillis();
        System.out.println("✅ 接続成功！ (所要時間: " + (endTime - startTime) + "ms)");
        System.out.println(
            "接続情報: "
                + conn.getMetaData().getDatabaseProductName()
                + " "
                + conn.getMetaData().getDatabaseProductVersion());

        // 簡単なクエリテスト
        try (var stmt = conn.createStatement();
            var rs = stmt.executeQuery("SELECT version()")) {
          if (rs.next()) {
            System.out.println("PostgreSQLバージョン: " + rs.getString(1));
          }
        }
      }

    } catch (ClassNotFoundException e) {
      System.out.println("❌ PostgreSQLドライバーが見つかりません: " + e.getMessage());
    } catch (SQLException e) {
      System.out.println("❌ 接続失敗: " + e.getMessage());
      System.out.println("SQLState: " + e.getSQLState());
      System.out.println("ErrorCode: " + e.getErrorCode());

      Throwable cause = e.getCause();
      if (cause != null) {
        System.out.println("原因: " + cause.getClass().getSimpleName() + " - " + cause.getMessage());
      }
    } catch (Exception e) {
      System.out.println("❌ 予期しないエラー: " + e.getClass().getSimpleName() + " - " + e.getMessage());
    }
  }
}
