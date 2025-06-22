package com.mycompany.webapp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * シンプルなRender PostgreSQL接続テスト Spring Bootを使わない単純なJDBC接続テスト
 */
public class SimpleConnectionTest {

    public static void main(String[] args) {
        // 接続情報
        String url =
                "jdbc:postgresql://dpg-d1j8h7he5dus7396aujg-a.singapore-postgres.render.com:5432/kakeibo_db?sslmode=require";
        String username = "kakeibo_user";
        String password = "LgjAtV1TaR5Uwot2ecX9fhi5UfDFqek3";

        System.out.println("=== Render PostgreSQL接続テスト ===");
        System.out.println("URL: " + url);
        System.out.println("Username: " + username);
        System.out.println("Password: " + password.substring(0, 5) + "****");

        try {
            // PostgreSQLドライバーをロード
            Class.forName("org.postgresql.Driver");
            System.out.println("✅ PostgreSQLドライバーロード成功");

            // データベース接続
            System.out.println("🔄 データベースへの接続を試行中...");
            Connection connection = DriverManager.getConnection(url, username, password);
            System.out.println("✅ データベース接続成功!");

            // 接続情報を表示
            System.out.println(
                    "Database Product: " + connection.getMetaData().getDatabaseProductName());
            System.out.println(
                    "Database Version: " + connection.getMetaData().getDatabaseProductVersion());
            System.out.println("Driver Name: " + connection.getMetaData().getDriverName());
            System.out.println("Driver Version: " + connection.getMetaData().getDriverVersion());

            // 接続をクローズ
            connection.close();
            System.out.println("✅ 接続をクローズしました");

        } catch (ClassNotFoundException e) {
            System.err.println("❌ PostgreSQLドライバーが見つかりません: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("❌ データベース接続エラー:");
            System.err.println("  SQLState: " + e.getSQLState());
            System.err.println("  Error Code: " + e.getErrorCode());
            System.err.println("  Message: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("❌ 予期しないエラー: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
