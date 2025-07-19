# 家計簿アプリ 単体テスト仕様書

## 📋 概要

本書は家計簿アプリのコア機能に限定した単体テスト計画書です。
MCPサーバー、LINE Bot機能、Render監視機能などの外部連携機能は除外し、家計簿の基本機能のみに焦点を当てています。

**作成日**: 2025年7月19日
**対象バージョン**: 現在のmainブランチ
**テストカバレッジ目標**: 全体90%以上

---

## 🎯 テスト対象範囲

### ✅ **テスト対象（家計簿コア機能）**
- **Model**: `Infokanri.java` - 収支データエンティティ
- **Repository**: `InfokanriRepository.java` - データアクセス層
- **Service**: `InfokanriService.java` - ビジネスロジック層
- **Controller**: `InfokanriController.java` - REST API層
- **Exception Handler**: `GlobalExceptionHandler.java` - 例外処理

### ❌ **テスト除外対象（家計簿機能に無関係）**
- `MCPLineBotService.java` - LINE Bot連携
- `NotificationService.java` - 通知サービス
- `RenderMonitoringService.java` - Render監視
- `DeployNotificationController.java` - デプロイ通知
- `ApplicationStartupListener.java` - アプリ起動処理
- `DatabaseConnectionChecker.java` - DB接続チェック
- `UserTokenController.java` - トークン管理

---

## 🧪 単体テスト詳細仕様

### **1. Model層テスト**

#### **`InfokanriTest.java`**
**対象クラス**: `com.mycompany.webapp.model.Infokanri`  
**テストタイプ**: Unit Test（POJO）

| メソッド名 | テスト内容 | 期待結果 |
|---|---|---|
| `testConstructorWithAllParameters()` | 全パラメータでの正常作成 | インスタンス正常生成 |
| `testConstructorDefault()` | デフォルトコンストラクタ | 空インスタンス生成 |
| `testSettersAndGetters()` | 各フィールドのsetter/getter | 値の正常設定・取得 |
| `testPrePersistLifecycle()` | @PrePersistでcreatedAt自動設定 | createdAtが現在時刻に設定 |
| `testPreUpdateLifecycle()` | @PreUpdateでupdatedAt自動設定 | updatedAtが現在時刻に設定 |
| `testValidTypeValues()` | 種別（収入/支出）の有効値検証 | 「収入」「支出」が正常設定 |
| `testInvalidAmountNegative()` | 負の金額の検証 | バリデーション動作確認 |
| `testInvalidAmountZero()` | ゼロ金額の検証 | ゼロ値の処理確認 |
| `testRegistedAtFuture()` | 未来日付の処理 | 未来日付の正常処理 |
| `testRegistedAtPast()` | 過去日付の処理 | 過去日付の正常処理 |
| `testToString()` | toString()メソッドの出力確認 | 適切な文字列表現 |
| `testEqualsAndHashCode()` | equals/hashCode契約 | オブジェクト等価性確認 |

---

### **2. Repository層テスト**

#### **`InfokanriRepositoryTest.java`**
**対象クラス**: `com.mycompany.webapp.repository.InfokanriRepository`  
**テストタイプ**: `@DataJpaTest`（JPA統合テスト）

| メソッド名 | テスト内容 | 期待結果 |
|---|---|---|
| `testSaveAndFindById()` | データ保存と検索 | 保存後にIDで正常取得 |
| `testFindAll()` | 全データ取得 | 全レコード正常取得 |
| `testFindAllEmpty()` | データが空の場合 | 空リスト返却 |
| `testDeleteById()` | ID指定削除 | 指定レコード削除 |
| `testDeleteByIdNotFound()` | 存在しないID削除 | 例外発生しない |
| `testExistsById()` | データ存在確認 | 存在チェック正常動作 |
| `testCountRecords()` | レコード数カウント | 正確なレコード数返却 |
| `testSaveMultipleRecords()` | 複数レコード保存 | 全レコード正常保存 |
| `testUpdateRecord()` | レコード更新 | 更新処理正常実行 |
| `testFindByUserIdCustomQuery()` | ユーザーID検索（将来実装） | ユーザー別データ取得 |
| `testDateRangeQuery()` | 日付範囲検索（将来実装） | 期間絞り込み検索 |

---

### **3. Service層テスト**

#### **`InfokanriServiceTest.java`**
**対象クラス**: `com.mycompany.webapp.service.InfokanriService`
**テストタイプ**: `@ExtendWith(MockitoExtension.class)`（Mock使用）

| メソッド名 | テスト内容 | 期待結果 |
|---|---|---|
| `testSaveInfokanri()` | 正常なデータ保存 | Repository.save()呼び出し確認 |
| `testSaveInfokanriWithNull()` | null値での保存試行 | 適切な例外処理 |
| `testGetAllInfokanri()` | 全データ取得 | Repository.findAll()呼び出し |
| `testGetAllInfokanriEmpty()` | 空のデータリスト取得 | 空リスト正常返却 |
| `testDeleteInfokanri()` | 正常なデータ削除 | Repository.deleteById()呼び出し |
| `testDeleteInfokanriNotFound()` | 存在しないID削除 | 例外発生せず正常終了 |
| `testDeleteInfokanriWithNull()` | null ID削除 | null値適切処理 |
| `testUpdateInfokanri()` | 正常なデータ更新 | Repository.save()で更新実行 |
| `testUpdateInfokanriWithNull()` | null値での更新 | null返却確認 |
| `testUpdateInfokanriNotFound()` | 存在しないデータ更新 | 適切なエラーハンドリング |
| `testGetReportData()` | レポートデータ取得 | 全データ返却確認 |
| `testGetReportDataWithParameters()` | パラメータ付きレポート取得 | パラメータ無視して全データ返却 |
| `testGetReportDataDatabaseError()` | DB接続エラー時の処理 | 空リスト返却（フォールバック） |
| `testTransactionalRollback()` | トランザクションロールバック | @Transactional動作確認 |

---

### **4. Controller層テスト**

#### **`InfokanriControllerTest.java`**
**対象クラス**: `com.mycompany.webapp.controller.InfokanriController`
**テストタイプ**: `@WebMvcTest`（Web層のみテスト）

| メソッド名 | テスト内容 | 期待結果 |
|---|---|---|
| `testAddInfokanri()` | 正常なデータ登録 | 200 OK + JSON形式レスポンス |
| `testAddInfokanriWithUserId()` | ユーザーID付きデータ登録 | X-User-Idヘッダー正常処理 |
| `testAddInfokanriInvalidData()` | 無効なデータでの登録 | 400 Bad Request |
| `testAddInfokanriDatabaseError()` | DB接続エラー時の処理 | 500 + エラーメッセージ |
| `testAddInfokanriRetryLogic()` | 再試行ロジックの動作確認 | 最大3回再試行実行 |
| `testGetAllInfokanri()` | 全データ取得API | 200 OK + データ配列 |
| `testGetAllInfokanriEmpty()` | 空データでのAPI応答 | 200 OK + 空配列 |
| `testDeleteInfokanri()` | データ削除API | 200 OK + 削除成功メッセージ |
| `testDeleteInfokanriNotFound()` | 存在しないデータ削除 | 適切なステータスコード |
| `testUpdateInfokanri()` | データ更新API | 200 OK + 更新後データ |
| `testUpdateInfokanriNotFound()` | 存在しないデータ更新 | 404 Not Found |
| `testGetReport()` | レポート取得API | 200 OK + レポートデータ |
| `testGetReportWithDateRange()` | 日付範囲指定レポート | パラメータ付きレポート取得 |
| `testContentTypeHeaders()` | レスポンスヘッダー確認 | Content-Type: application/json |
| `testErrorResponseFormat()` | エラー時のJSONフォーマット | 統一されたエラー形式 |

---

### **5. Exception Handler テスト**

#### **`GlobalExceptionHandlerTest.java`**
**対象クラス**: `com.mycompany.webapp.controller.GlobalExceptionHandler`
**テストタイプ**: Unit Test

| メソッド名 | テスト内容 | 期待結果 |
|---|---|---|
| `testHandleGenericException()` | 一般例外処理 | 500 + JSON エラーレスポンス |
| `testHandleRuntimeException()` | ランタイム例外処理 | 500 + 適切なエラーメッセージ |
| `testExceptionResponseFormat()` | 例外レスポンスのJSON形式 | 統一されたJSON形式 |
| `testExceptionTimestamp()` | 例外時のタイムスタンプ | timestamp フィールド存在 |
| `testExceptionStatusCode()` | HTTPステータスコード確認 | 適切なHTTPステータス |
| `testExceptionLogging()` | 例外ログ出力確認 | System.err への出力確認 |

---

### **6. 統合テスト**

#### **`InfokanriIntegrationTest.java`**
**対象クラス**: 全レイヤー統合
**テストタイプ**: `@SpringBootTest`（全レイヤー統合テスト）

| メソッド名 | テスト内容 | 期待結果 |
|---|---|---|
| `testCompleteDataFlow()` | データ登録→取得→更新→削除の完全フロー | 全CRUD操作正常実行 |
| `testMultiUserData()` | 複数ユーザーデータの分離 | ユーザー別データ管理 |
| `testConcurrentAccess()` | 同時アクセス処理 | 並行処理での整合性 |
| `testDatabaseTransaction()` | データベーストランザクション | トランザクション境界確認 |
| `testErrorRecovery()` | エラー後の復旧処理 | 障害発生後の正常復帰 |
| `testDataConsistency()` | データ整合性確認 | データの一貫性保証 |

---

## 📊 テストカバレッジ目標

| レイヤー | カバレッジ目標 | 理由 |
|---|---|---|
| **Model** | 100% | シンプルなPOJO、全パス実行容易 |
| **Repository** | 90%以上 | JPA標準機能中心、カスタムクエリ重点 |
| **Service** | 95%以上 | ビジネスロジック中核、重要度最高 |
| **Controller** | 90%以上 | REST API、エラーハンドリング重点 |
| **Exception Handler** | 100% | 例外処理、全パターン実行必須 |
| **全体** | 90%以上 | 品質保証の最低基準 |

---

## 🛠️ 使用テストライブラリ・フレームワーク

### **主要ライブラリ**
- **JUnit 5**: テストフレームワーク
- **Mockito**: モックオブジェクト作成
- **AssertJ**: 流暢なアサーション
- **Hamcrest**: マッチャーライブラリ

### **Spring Boot Test**
- **@SpringBootTest**: 全レイヤー統合テスト
- **@WebMvcTest**: Web層のみテスト
- **@DataJpaTest**: JPA Repository テスト
- **@ExtendWith(MockitoExtension.class)**: Mockito統合

### **オプション**
- **TestContainers**: 実データベースでのテスト
- **WireMock**: 外部API モック

---

## 🗂️ テストファイル構成

```
src/test/java/com/mycompany/webapp/
├── model/
│   └── InfokanriTest.java
├── repository/
│   └── InfokanriRepositoryTest.java
├── service/
│   └── InfokanriServiceTest.java
├── controller/
│   ├── InfokanriControllerTest.java
│   └── GlobalExceptionHandlerTest.java
└── integration/
    └── InfokanriIntegrationTest.java
```

---

## ✅ 実装チェックリスト

### **Phase 1: 基本テスト実装**
- [ ] Model層テスト（InfokanriTest.java）
- [ ] Repository層テスト（InfokanriRepositoryTest.java）
- [ ] Service層テスト（InfokanriServiceTest.java）

### **Phase 2: Web層テスト実装**
- [ ] Controller層テスト（InfokanriControllerTest.java）
- [ ] Exception Handler テスト（GlobalExceptionHandlerTest.java）

### **Phase 3: 統合テスト実装**
- [ ] 統合テスト（InfokanriIntegrationTest.java）

### **Phase 4: 品質確認**
- [ ] テストカバレッジ確認
- [ ] 継続的インテグレーション設定
- [ ] テスト実行自動化

---

## 📝 注意事項

### **除外理由**
以下のクラスは家計簿の基本機能に直接関係しないため、テスト対象から除外:
- LINE Bot連携機能
- Render監視・通知機能
- MCP サーバー機能
- デプロイ関連機能

### **将来的な拡張**
- ユーザー認証機能追加時のテスト拡張
- レポート機能強化時の追加テスト
- パフォーマンステスト（必要に応じて）

---

**最終更新**: 2025年7月19日
**次回レビュー予定**: テスト実装完了後
