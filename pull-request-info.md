# プルリクエスト情報

## タイトル
feat: Supabaseテーブル構造への移行対応

## 説明文

### 概要
Supabaseの新しいテーブル構造に対応するための包括的な移行作業です。

### 変更内容

#### データベース
- **schema.sql**: PostgreSQL/Supabase対応の新しいテーブル構造
- 日本語フィールド名への変更（user_token→user_id, type→syubetu等）
- 自動タイムスタンプフィールド追加（created_at, updated_at）

#### バックエンド（Java Spring Boot）
- **Infokanriモデル**: 新しいカラム構造に対応
- **InfokanriController**: ヘッダー名をX-User-Token→X-User-Idに変更
- ライフサイクルメソッド追加（@PrePersist, @PreUpdate）

#### フロントエンド（JavaScript）
- **inexpen.js**: 新しいAPIペイロード形式に対応
- **list.js**: 新しいレスポンス構造に対応
- **report.js**: 新しいデータ形式に対応
- ユーザートークン→ユーザーIDシステムに変更

#### ドキュメント・設定
- **supabase-table-setup.sql**: Supabaseテーブル作成用SQL
- **supabase-migration-guide.md**: 詳細な移行ガイド
- **render-env-config.md**: 環境変数設定ガイド
- **DatabaseConnectionTest.java**: 接続テスト用ユーティリティ

### テーブル構造変更
| 旧カラム名 | 新カラム名 | 変更内容 |
|------------|------------|----------|
| user_token | user_id | ユーザー識別方式変更 |
| registered_at | hiduke | 日本語フィールド名 |
| type | syubetu | 英語→日本語値 |
| category | naisyo | 日本語フィールド名 |
| amount | kingaku | 日本語フィールド名 |
| update_date_time | created_at, updated_at | タイムスタンプ分離 |

### 破壊的変更
- APIペイロード構造の変更
- ユーザー識別システムの変更（トークン→ID）
- データベーススキーマの変更

### テスト手順
1. Supabaseでテーブル作成（supabase-table-setup.sql使用）
2. Render環境変数更新
3. アプリケーション再デプロイ
4. 収支登録→一覧表示→レポート生成の動作確認

### 関連Issue
- データがSupabaseに登録されない問題の解決
- 新しいテーブル構造への移行要件対応
