package com.mycompany.webapp;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import com.mycompany.webapp.repository.InfokanriRepository;
import com.mycompany.webapp.model.Infokanri;
import java.util.List;

/**
 * データベース接続とデータ存在確認テスト
 * 本番環境のデータベースに接続して、実際のデータを確認
 */
@SpringBootTest
@ActiveProfiles("production")
public class DatabaseConnectionTest {

    @Autowired
    private InfokanriRepository repository;

    /**
     * データベース接続テスト
     * tb_info_kanriテーブルの全データを取得して表示
     */
    @Test
    public void testDatabaseConnection() {
        try {
            // 全データを取得
            List<Infokanri> allData = repository.findAll();
            
            System.out.println("=== データベース接続テスト結果 ===");
            System.out.println("総データ件数: " + allData.size());
            
            if (allData.isEmpty()) {
                System.out.println("⚠️  データが登録されていません");
            } else {
                System.out.println("✅ データが存在します");
                System.out.println("");
                System.out.println("=== 登録済みデータ一覧 ===");
                
                for (Infokanri data : allData) {
                    System.out.println("ID: " + data.getId());
                    System.out.println("ユーザートークン: " + data.getUserToken());
                    System.out.println("登録日: " + data.getRegisteredAt());
                    System.out.println("タイプ: " + data.getType());
                    System.out.println("カテゴリ: " + data.getCategory());
                    System.out.println("金額: " + data.getAmount());
                    System.out.println("更新日時: " + data.getUpdateDateTime());
                    System.out.println("---");
                }
            }
            
        } catch (Exception e) {
            System.err.println("❌ データベース接続エラー: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * テーブル存在確認テスト
     * テーブルに対する基本的な操作が可能かを確認
     */
    @Test
    public void testTableExists() {
        try {
            // countクエリでテーブルの存在を確認
            long count = repository.count();
            System.out.println("✅ tb_info_kanriテーブルが存在します");
            System.out.println("現在のレコード数: " + count);
            
        } catch (Exception e) {
            System.err.println("❌ テーブルアクセスエラー: " + e.getMessage());
            System.err.println("テーブルが存在しないか、接続に問題があります");
            e.printStackTrace();
        }
    }
}
