package com.mycompany.webapp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Render PostgreSQL接続テスト
 */
@SpringBootTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:postgresql://dpg-d1j8h7he5dus7396aujg-a.singapore-postgres.render.com:5432/kakeibo_db?sslmode=require",
    "spring.datasource.username=kakeibo_user",
    "spring.datasource.password=LgjAtV1TaR5Uwot2ecX9fhi5UfDFqek3",
    "spring.datasource.driver-class-name=org.postgresql.Driver"
})
public class RenderPostgreSQLConnectionTest {

    @Test
    public void testRenderPostgreSQLConnection() {
        String url = "jdbc:postgresql://dpg-d1j8h7he5dus7396aujg-a.singapore-postgres.render.com:5432/kakeibo_db?sslmode=require";
        String username = "kakeibo_user";
        String password = "LgjAtV1TaR5Uwot2ecX9fhi5UfDFqek3";

        try {
            System.out.println("=== Render PostgreSQL接続テスト開始 ===");
            
            // データベース接続
            Connection connection = DriverManager.getConnection(url, username, password);
            System.out.println("✅ データベース接続成功: " + connection.getMetaData().getDatabaseProductName());
            
            // テーブル一覧確認
            Statement statement = connection.createStatement();
            ResultSet tables = connection.getMetaData().getTables(null, null, "tb_info_kanri", new String[]{"TABLE"});
            
            if (tables.next()) {
                System.out.println("✅ テーブル tb_info_kanri が存在します");
                
                // データ件数確認
                ResultSet rs = statement.executeQuery("SELECT COUNT(*) as count FROM tb_info_kanri");
                if (rs.next()) {
                    System.out.println("📊 データ件数: " + rs.getInt("count") + "件");
                }
                rs.close();
                
                // サンプルデータ確認
                rs = statement.executeQuery("SELECT * FROM tb_info_kanri ORDER BY id DESC LIMIT 3");
                System.out.println("📋 最新データ:");
                while (rs.next()) {
                    System.out.printf("  ID: %d, 種別: %s, 金額: %d円, 内容: %s, 日付: %s%n",
                        rs.getLong("id"),
                        rs.getString("syubetu"),
                        rs.getInt("kingaku"),
                        rs.getString("naisyo"),
                        rs.getDate("hiduke"));
                }
                rs.close();
            } else {
                System.out.println("⚠️  テーブル tb_info_kanri が見つかりません - テーブル作成が必要です");
            }
            
            statement.close();
            connection.close();
            System.out.println("=== 接続テスト完了 ===");
            
        } catch (Exception e) {
            System.err.println("❌ 接続エラー: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
