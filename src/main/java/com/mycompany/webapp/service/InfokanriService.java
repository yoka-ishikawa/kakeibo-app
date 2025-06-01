package com.mycompany.webapp.service;

import org.springframework.stereotype.Service;
import com.mycompany.webapp.model.Infokanri; // Entityをインポート
import com.mycompany.webapp.repository.InfokanriRepository; // Rrpositoryをインポート
import jakarta.transaction.Transactional;

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

}
