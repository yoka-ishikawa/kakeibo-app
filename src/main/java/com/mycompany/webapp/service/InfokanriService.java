package com.mycompany.webapp.service;

import org.springframework.stereotype.Service;
import com.mycompany.webapp.model.Infokanri;
import com.mycompany.webapp.repository.InfokanriRepository;
import jakarta.transaction.Transactional;
import java.util.List;

/**
 * 家計簿データのビジネスロジックを処理するサービスクラス
 * データアクセス層（Repository）とコントローラー層の中間層として機能
 */
@Service
public class InfokanriService {
    
    private final InfokanriRepository repository;

    /**
     * コンストラクターインジェクションによるリポジトリの依存性注入
     */
    public InfokanriService(InfokanriRepository repository) {
        this.repository = repository;
    }

    /**
     * 収支データをデータベースに保存
     * @param infokanri 保存する収支データ
     * @return 保存されたデータ（IDや更新日時などが設定済み）
     */
    @Transactional
    public Infokanri saveInfokanri(Infokanri infokanri) {
        return repository.save(infokanri);
    }

    /**
     * 全ての収支データを取得
     * @return 全収支データのリスト
     */
    public List<Infokanri> getAllInfokanri() {
        return repository.findAll();
    }

    /**
     * 指定されたIDの収支データを削除
     * 存在しないIDの場合は何もしない（エラーを発生させない）
     * @param id 削除対象のデータID
     */
    @Transactional
    public void deleteInfokanri(Long id) {
        if (id != null && repository.existsById(id)) {
            repository.deleteById(id);
        }
    }

    /**
     * 収支データを更新
     * JPAのsaveメソッドはIDが設定されている場合、更新処理を実行
     * @param infokanri 更新する収支データ（ID必須）
     * @return 更新されたデータ、nullの場合はnullを返す
     */
    @Transactional
    public Infokanri updateInfokanri(Infokanri infokanri) {
        if (infokanri != null) {
            return repository.save(infokanri);
        }
        return null;
    }

    /**
     * レポート画面用のデータを取得
     * 現在は全データを返す簡易実装。将来的には期間フィルタリング機能を実装予定
     * @param period 期間指定（現在未使用）
     * @param startDate 開始日（現在未使用）
     * @param endDate 終了日（現在未使用）
     * @return 収支データのリスト、エラー時は空のリスト
     */
    public List<Infokanri> getReportData(String period, String startDate, String endDate) {
        try {
            // TODO: 将来的に期間指定によるフィルタリングを実装
            // 例: repository.findByRegisteredAtBetween(startDate, endDate)
            return repository.findAll();
        } catch (Exception e) {
            // データベースアクセスエラー時のフォールバック
            return new java.util.ArrayList<>();
        }
    }

}
