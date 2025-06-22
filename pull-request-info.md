# プルリクエスト情報

## タイトル

feat: Supabase テーブル構造への移行対応

## 説明文

### 概要

Supabase の新しいテーブル構造に対応するための包括的な移行作業です。

### 変更内容

#### データベース

- **schema.sql**: PostgreSQL/Supabase 対応の新しいテーブル構造
- 日本語フィールド名への変更（user_token→user_id, type→syubetu 等）
- 自動タイムスタンプフィールド追加（created_at, updated_at）

#### バックエンド（Java Spring Boot）

- **Infokanri モデル**: 新しいカラム構造に対応
- **InfokanriController**: ヘッダー名を X-User-Token→X-User-Id に変更
- ライフサイクルメソッド追加（@PrePersist, @PreUpdate）

#### フロントエンド（JavaScript）

- **inexpen.js**: 新しい API ペイロード形式に対応
- **list.js**: 新しいレスポンス構造に対応
- **report.js**: 新しいデータ形式に対応
- ユーザートークン → ユーザー ID システムに変更

#### ドキュメント・設定

- **supabase-table-setup.sql**: Supabase テーブル作成用 SQL
- **supabase-migration-guide.md**: 詳細な移行ガイド
- **render-env-config.md**: 環境変数設定ガイド
- **DatabaseConnectionTest.java**: 接続テスト用ユーティリティ

### テーブル構造変更

| 旧カラム名       | 新カラム名             | 変更内容             |
| ---------------- | ---------------------- | -------------------- |
| user_token       | user_id                | ユーザー識別方式変更 |
| registered_at    | hiduke                 | 日本語フィールド名   |
| type             | syubetu                | 英語 → 日本語値      |
| category         | naisyo                 | 日本語フィールド名   |
| amount           | kingaku                | 日本語フィールド名   |
| update_date_time | created_at, updated_at | タイムスタンプ分離   |

### 破壊的変更

- API ペイロード構造の変更
- ユーザー識別システムの変更（トークン →ID）
- データベーススキーマの変更

### テスト手順

1. Supabase でテーブル作成（supabase-table-setup.sql 使用）
2. Render 環境変数更新
3. アプリケーション再デプロイ
4. 収支登録 → 一覧表示 → レポート生成の動作確認

### 関連 Issue

- データが Supabase に登録されない問題の解決
- 新しいテーブル構造への移行要件対応
