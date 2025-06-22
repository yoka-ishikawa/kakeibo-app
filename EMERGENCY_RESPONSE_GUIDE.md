# 家計簿アプリ緊急対応ガイド（PostgreSQL接続エラー）

## 🚨 現在の状況
- **問題**: 本番環境で収支登録が500エラーで失敗
- **根本原因**: Render PostgreSQL接続時のSSL/TLS認証エラー
- **エラー**: `java.io.EOFException` - データベースサーバーとの接続が切断される

## 🔧 緊急対応完了項目

### 1. データベース接続強化
- ✅ カスタムデータソース設定（DatabaseConfig.java）
- ✅ 複数接続オプション（SSL/非SSL自動切替）
- ✅ 接続タイムアウト・リトライ機能
- ✅ 詳細なエラーログ出力

### 2. 設定ファイル最適化
- ✅ application-production.properties更新
- ✅ HikariCP接続プール設定強化
- ✅ PostgreSQL固有オプション追加

### 3. 診断ツール追加
- ✅ DatabaseConnectionChecker.java（接続テスト）
- ✅ 起動時環境変数詳細出力
- ✅ ヘルスチェックAPI (/api/infokanri/health)

## 🎯 次のアクション（緊急）

### Phase 1: Render環境変数確認・修正
1. **Renderダッシュボード**を開く
2. **Environment Variables**で以下を確認・設定:
   ```
   DATABASE_URL=jdbc:postgresql://dpg-d12hm7je5dus7396aujg-a.singapore-postgres.render.com:5432/tb_info_kanri?sslmode=require&connectTimeout=60&socketTimeout=60&tcpKeepAlive=true&ApplicationName=kakeibo-app
   DB_USERNAME=tb_info_kanri_user
   DB_PASSWORD=[正しいパスワード]
   JPA_DIALECT=org.hibernate.dialect.PostgreSQLDialect
   SPRING_PROFILES_ACTIVE=production
   ```

### Phase 2: PostgreSQLデータベース状態確認
1. **Render Dashboardで Database状態確認**
2. **Connection Detailsが正しいか確認**
3. **データベースサーバーが稼働中か確認**

### Phase 3: アプリケーション再デプロイ・テスト
1. **Manual Deploy**実行
2. **起動ログで環境変数出力確認**
3. **ヘルスチェックAPI**テスト: `https://[アプリURL]/api/infokanri/health`
4. **収支登録機能**テスト

## 🔍 トラブルシューティング手順

### A. 接続情報確認
```bash
# ヘルスチェック
curl https://[アプリURL]/api/infokanri/health

# 詳細ログ確認
Render Dashboard -> Logs -> 起動ログをチェック
```

### B. 段階的接続テスト
1. **SSL有効**: `sslmode=require`
2. **SSL無効**: `sslmode=disable`（緊急時のみ）
3. **SSL優先**: `sslmode=prefer`

### C. 環境変数デバッグ
起動ログで以下が正しく表示されるか確認:
- DATABASE_URL
- DB_USERNAME
- DB_PASSWORD（設定済み表示）

## 📞 緊急連絡・対応フロー

### 即座に対応が必要な場合:
1. **Render Dashboard**でManual Deploy
2. **ヘルスチェックAPI**で接続確認
3. **本メッセージをチーム共有**

### 問題が解決しない場合:
1. **データベース再作成**を検討
2. **Render Supportへ問い合わせ**
3. **バックアップからの復旧**準備

## 🎉 成功確認項目
- [ ] ヘルスチェックAPI が 200 OK
- [ ] 収支登録が正常完了
- [ ] データがデータベースに保存される
- [ ] フロントエンドでエラー表示されない

## 📋 今後の予防策
- 定期的なヘルスチェック監視
- データベース接続監視アラート
- 自動テスト環境の構築
- 障害時の自動通知システム

---
**更新日時**: 2024年12月19日
**対応者**: GitHub Copilot
**緊急度**: 🔴 最高（事業継続に影響）
