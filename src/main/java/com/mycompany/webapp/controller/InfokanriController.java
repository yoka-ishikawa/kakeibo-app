package com.mycompany.webapp.controller;

import com.mycompany.webapp.model.Infokanri;
import com.mycompany.webapp.service.InfokanriService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/** 家計簿データ管理用REST APIコントローラー 収支データのCRUD操作とレポート機能を提供 */
@RestController
@RequestMapping("/api/infokanri")
public class InfokanriController {

  private final InfokanriService service;

  /** コンストラクターインジェクションによるサービス層の依存性注入 */
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
  @PostMapping(produces = "application/json")
  public ResponseEntity<String> add(@RequestBody Infokanri infokanri,
      @RequestHeader(value = "X-User-Id", defaultValue = "anonymous") String userId) {
    System.out.println("=== 収支データ登録開始 ===");
    System.out.println("User ID: " + userId);
    System.out.println("受信データ: " + infokanri.toString());

    try {
      infokanri.setUserId(userId);
      System.out.println("UserID設定完了");

      Infokanri savedData = service.saveInfokanri(infokanri);
      System.out.println("データベース保存完了");
      System.out.println("保存されたデータ: " + savedData.toString());

      // 一時的にシンプルなJSONレスポンスを返す
      String jsonResponse = String.format(
          "{\"id\": %d, \"userId\": \"%s\", \"syubetu\": \"%s\", \"kingaku\": %d, \"naisyo\": \"%s\", \"hiduke\": \"%s\"}",
          savedData.getId(), savedData.getUserId(), savedData.getSyubetu(), savedData.getKingaku(),
          savedData.getNaisyo(), savedData.getHiduke().toString());

      System.out.println("手動JSON生成完了: " + jsonResponse);

      return ResponseEntity.ok().header("Content-Type", "application/json").body(jsonResponse);

    } catch (Exception e) {
      System.err.println("=== エラー発生 ===");
      System.err.println("エラー内容: " + e.getMessage());
      e.printStackTrace();

      // エラー時のレスポンス
      return ResponseEntity.internalServerError().header("Content-Type", "application/json")
          .body("{\"error\": \"データ登録に失敗しました\"}");
    }
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
  @PutMapping(value = "/{id}", produces = "application/json")
  public ResponseEntity<Infokanri> updateData(@PathVariable Long id,
      @RequestBody Infokanri infokanri,
      @RequestHeader(value = "X-User-Id", defaultValue = "anonymous") String userId) {
    System.out.println("=== 収支データ更新 ===");
    System.out.println("ID: " + id);
    System.out.println("User ID: " + userId);
    System.out.println("更新データ: " + infokanri.toString());

    infokanri.setId(id);
    infokanri.setUserId(userId);
    Infokanri updatedData = service.updateInfokanri(infokanri);

    System.out.println("更新されたデータ: " + updatedData.toString());
    return ResponseEntity.ok().header("Content-Type", "application/json").body(updatedData);
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

  /**
   * データベース接続テスト用エンドポイント
   * 
   * @return データベース接続状況
   */
  @GetMapping("/health")
  public ResponseEntity<Map<String, Object>> healthCheck() {
    Map<String, Object> health = new HashMap<>();
    try {
      // データベースから件数を取得してテスト
      List<Infokanri> testData = service.getAllInfokanri();
      health.put("status", "OK");
      health.put("database", "Connected");
      health.put("recordCount", testData.size());
      health.put("timestamp", java.time.LocalDateTime.now().toString());

      return ResponseEntity.ok().header("Content-Type", "application/json").body(health);
    } catch (Exception e) {
      health.put("status", "ERROR");
      health.put("database", "Connection Failed");
      health.put("error", e.getMessage());
      health.put("timestamp", java.time.LocalDateTime.now().toString());

      return ResponseEntity.status(500).header("Content-Type", "application/json").body(health);
    }
  }
}
