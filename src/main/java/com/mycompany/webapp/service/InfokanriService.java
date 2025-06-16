package com.mycompany.webapp.service;

import org.springframework.stereotype.Service;
import com.mycompany.webapp.model.Infokanri; // Entityをインポート
import com.mycompany.webapp.repository.InfokanriRepository; // Rrpositoryをインポート
import jakarta.transaction.Transactional;
import java.util.List;

// InfokanriRepositoryを使用してDB操作を行うサービスクラス
@Service
public class InfokanriService {
    // InfokanriRepositoryのインスタンスを保持
    private final InfokanriRepository repository;

    public InfokanriService(InfokanriRepository repository) {
        this.repository = repository;
    }

    // DBに登録するメソッド（Infokanriオブジェクトを受け取り、保存して返す）
    @Transactional
    public Infokanri saveInfokanri(Infokanri infokanri) {
        return repository.save(infokanri);
    }

    // すべてのデータを取得するメソッド
    public List<Infokanri> getAllInfokanri() {
        return repository.findAll();
    }

    // データを削除するメソッド
    @Transactional
    public void deleteInfokanri(Long id) {
        repository.deleteById(id);
    }

    // データを更新するメソッド
    @Transactional
    public Infokanri updateInfokanri(Infokanri infokanri) {
        return repository.save(infokanri);
    }

    // レポート用データを取得するメソッド
    public List<Infokanri> getReportData(String period, String startDate, String endDate) {
        // 簡易実装：現状では全データを返す
        // 実際の実装では期間フィルタリングを行う
        return repository.findAll();
    }

}
