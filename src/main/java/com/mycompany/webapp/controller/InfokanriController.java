package com.mycompany.webapp.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import com.mycompany.webapp.model.Infokanri;
import com.mycompany.webapp.service.InfokanriService;
import java.util.List;

/**
 * 家計簿データ管理用REST APIコントローラー 収支データのCRUD操作とレポート機能を提供
 */
@RestController
@RequestMapping("/api/infokanri")
public class InfokanriController {

    private final InfokanriService service;

    /**
     * コンストラクターインジェクションによるサービス層の依存性注入
     */
    public InfokanriController(InfokanriService service) {
        this.service = service;
    }

    /**
     * 新しい収支データを登録
     * 
     * @param infokanri 登録する収支データ
     * @param userId ユーザーID（リクエストヘッダーから取得）
     * @return 登録された収支データ（IDを含む）
     */
    @PostMapping
    public Infokanri add(@RequestBody Infokanri infokanri,
            @RequestHeader(value = "X-User-Id", defaultValue = "anonymous") String userId) {
        System.out.println("=== 収支データ登録 ===");
        System.out.println("User ID: " + userId);
        System.out.println("受信データ: " + infokanri.toString());

        infokanri.setUserId(userId);
        Infokanri savedData = service.saveInfokanri(infokanri);

        System.out.println("保存されたデータ: " + savedData.toString());
        return savedData;
    }

    /**
     * 全ての収支データを取得
     * 
     * @return 全収支データのリスト
     */
    @GetMapping
    public List<Infokanri> getAllData() {
        return service.getAllInfokanri();
    }

    /**
     * 指定されたIDの収支データを削除
     * 
     * @param id 削除対象のデータID
     * @return HTTP 200 (成功) または HTTP 404 (データが見つからない)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteData(@PathVariable Long id) {
        try {
            service.deleteInfokanri(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 既存の収支データを更新
     * 
     * @param id 更新対象のデータID
     * @param infokanri 更新内容
     * @param userId ユーザーID
     * @return 更新された収支データ
     */
    @PutMapping("/{id}")
    public Infokanri updateData(@PathVariable Long id, @RequestBody Infokanri infokanri,
            @RequestHeader(value = "X-User-Id", defaultValue = "anonymous") String userId) {
        infokanri.setId(id);
        infokanri.setUserId(userId);
        return service.updateInfokanri(infokanri);
    }

    /**
     * レポート画面用のデータを取得 現在は全データを返すが、将来的には期間フィルタリング機能を実装予定
     * 
     * @param period 期間指定（月間/年間）
     * @param startDate 開始日
     * @param endDate 終了日
     * @return フィルタリングされた収支データのリスト
     */
    @GetMapping("/report")
    public List<Infokanri> getReportData(@RequestParam(required = false) String period,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return service.getReportData(period, startDate, endDate);
    }
}
