# Render PostgreSQL 完全設定ガイド

## ステップ 1: Render 環境変数設定

### 必要な環境変数（kakeibo-app サービス）

以下の環境変数を Render ダッシュボード（Environment タブ）で設定：

```bash
# データベース接続情報
DATABASE_URL=jdbc:postgresql://dpg-d12hm7je5dus7396aujg-a.singapore-postgres.render.com:5432/tb_info_kanri?sslmode=require
DB_DRIVER=org.postgresql.Driver
DB_USERNAME=kakeibo_user
DB_PASSWORD=LgjAtV1TaR5Uwot2ecX9fhi5UfDFqek3
JPA_DIALECT=org.hibernate.dialect.PostgreSQLDialect

# アプリケーション設定
SPRING_PROFILES_ACTIVE=production
PORT=10000
```

### 設定手順

1. Render ダッシュボード → kakeibo-app サービス → Environment タブ
2. 上記の環境変数を一つずつ設定
3. 「Save Changes」をクリック

## ステップ 2: データベースにテーブル作成

### 方法 1: Render ダッシュボードから（推奨）

1. **接続**

   - Render ダッシュボード → `kakeibo-db` データベース
   - 「Connect」ボタンをクリック
   - Web SQL エディタが開きます

2. **テーブル作成 SQL 実行**
   以下の SQL をコピー＆ペーストして実行：

```sql
-- 既存のテーブルを削除（必要に応じて）
-- DROP TABLE IF EXISTS tb_info_kanri;

-- 家計簿情報管理テーブル作成
CREATE TABLE IF NOT EXISTS tb_info_kanri (
    id SERIAL PRIMARY KEY,
    user_id VARCHAR(255),
    syubetu VARCHAR(50) NOT NULL,
    kingaku INTEGER NOT NULL,
    naisyo VARCHAR(255),
    hiduke DATE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- インデックス作成（パフォーマンス向上）
CREATE INDEX IF NOT EXISTS idx_tb_info_kanri_user_id ON tb_info_kanri(user_id);
CREATE INDEX IF NOT EXISTS idx_tb_info_kanri_hiduke ON tb_info_kanri(hiduke);
CREATE INDEX IF NOT EXISTS idx_tb_info_kanri_syubetu ON tb_info_kanri(syubetu);

-- サンプルデータ挿入（テスト用）
INSERT INTO tb_info_kanri (user_id, syubetu, kingaku, naisyo, hiduke) VALUES
('test-user-1', '収入', 300000, '給与', '2025-06-01'),
('test-user-1', '支出', 80000, '家賃', '2025-06-01'),
('test-user-1', '支出', 15000, '食費', '2025-06-15'),
('anonymous_user_1719037439000', '収入', 250000, '給与', '2025-06-22'),
('anonymous_user_1719037439000', '支出', 50000, '食費', '2025-06-22');

-- テーブル確認
\d tb_info_kanri

-- データ確認
SELECT * FROM tb_info_kanri ORDER BY hiduke DESC, id DESC;
```

### 方法 2: コマンドライン（上級者向け）

```bash
PGPASSWORD=LgjAtV1TaR5Uwot2ecX9fhi5UfDFqek3 psql -h dpg-d12hm7je5dus7396aujg-a.singapore-postgres.render.com -U kakeibo_user tb_info_kanri
```

## ステップ 3: アプリケーション再デプロイ

1. Render ダッシュボード → kakeibo-app サービス
2. 「Deploy latest commit」ボタンをクリック
3. デプロイログを確認

## ステップ 4: 動作確認

### 期待される成功ログ

```
HikariPool-1 - Starting...
HikariPool-1 - Start completed.
Started WebappApplication in X.XXX seconds
```

### テスト手順

1. **収支登録画面**: データ登録テスト
2. **一覧画面**: 登録データ表示確認
3. **レポート画面**: 集計機能確認

## トラブルシューティング

### よくあるエラーと対処法

1. **接続エラー**

   - 環境変数の値にスペースが入っていないか確認
   - PASSWORD に特殊文字が含まれる場合はエスケープ確認

2. **テーブルが見つからない**

   - テーブル作成 SQL が正常に実行されたか確認
   - テーブル名の大文字小文字を確認

3. **権限エラー**
   - kakeibo_user にテーブル作成権限があるか確認

### 確認コマンド

```sql
-- テーブル一覧
\dt

-- テーブル構造確認
\d tb_info_kanri

-- データ件数確認
SELECT COUNT(*) FROM tb_info_kanri;
```
