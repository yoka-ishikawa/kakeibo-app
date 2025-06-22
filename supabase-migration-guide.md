# Supabaseテーブル設定ガイド

## 1. Supabaseでテーブル作成

### ダッシュボードでの手順
1. Supabaseダッシュボードにログイン
2. プロジェクト `kakeibo-db` を選択
3. 左メニューから「Table Editor」を選択
4. 「Create a new table」をクリック
5. 以下の設定でテーブルを作成：

**テーブル名**: `tb_info_kanri`

**カラム設定**:
| カラム名 | データ型 | 制約 | デフォルト値 |
|---------|----------|------|-------------|
| id | int8 | Primary Key, Auto-increment | |
| user_id | varchar | | |
| syubetu | varchar(50) | NOT NULL | |
| kingaku | int4 | NOT NULL | |
| naisyo | varchar | | |
| hiduke | date | NOT NULL | |
| created_at | timestamptz | | now() |
| updated_at | timestamptz | | now() |

### SQLコマンドでの作成
または、「SQL Editor」で以下のSQLを実行：

```sql
-- 既存テーブルがある場合は削除（注意：データも削除されます）
-- DROP TABLE IF EXISTS tb_info_kanri;

-- 新しいテーブル作成
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
('test-user-1', '収入', 5000, 'アルバイト', '2025-06-20'),
('anonymous_user_1234567890', '収入', 250000, '給与', '2025-06-22'),
('anonymous_user_1234567890', '支出', 50000, '食費', '2025-06-22');
```

## 2. RLS（Row Level Security）設定

Supabaseでは、デフォルトでRLSが有効になっている場合があります。
アプリケーションが正常に動作するよう、適切なポリシーを設定してください。

### RLSを無効にする場合（テスト用）
```sql
ALTER TABLE tb_info_kanri DISABLE ROW LEVEL SECURITY;
```

### RLSを有効にして適切なポリシーを設定する場合
```sql
-- RLSを有効化
ALTER TABLE tb_info_kanri ENABLE ROW LEVEL SECURITY;

-- 全操作を許可するポリシー（本格運用では制限を検討）
CREATE POLICY "Allow all operations" ON tb_info_kanri FOR ALL USING (true);
```

## 3. 接続確認

### テーブル確認
```sql
-- テーブル構造確認
SELECT 
    table_name, 
    column_name, 
    data_type, 
    is_nullable, 
    column_default
FROM information_schema.columns 
WHERE table_name = 'tb_info_kanri' 
ORDER BY ordinal_position;

-- データ確認
SELECT * FROM tb_info_kanri ORDER BY hiduke DESC, id DESC LIMIT 10;
```

## 4. アプリケーション設定

### Render環境変数（最新版）
```bash
SPRING_PROFILES_ACTIVE=production
DATABASE_URL=jdbc:postgresql://db.gwtjqewcrchqjsywvqjc.supabase.co:5432/postgres?sslmode=require
DB_DRIVER=org.postgresql.Driver
DB_USERNAME=postgres
DB_PASSWORD=141726qooq
JPA_DIALECT=org.hibernate.dialect.PostgreSQLDialect
```

## 5. 変更点まとめ

### データベーススキーマ
- テーブル名: `tb_info_kanri` (変更なし)
- カラム変更:
  - `user_token` → `user_id`
  - `registered_at` → `hiduke`
  - `type` → `syubetu` (値も英語から日本語に)
  - `category` → `naisyo`
  - `amount` → `kingaku`
  - `update_date_time` → `created_at`, `updated_at`

### APIエンドポイント
- エンドポイント: `/api/infokanri` (変更なし)
- リクエストヘッダー: `X-User-Token` → `X-User-Id`
- リクエストボディ: 上記カラム変更に対応

### フロントエンド
- JavaScriptファイル更新: `inexpen.js`, `list.js`, `report.js`
- ユーザートークン → ユーザーIDに変更
- APIペイロード形式を新しいテーブル構造に対応

## 6. テスト手順

1. Supabaseでテーブル作成
2. Render環境変数更新
3. アプリケーション再デプロイ
4. 収支登録画面でデータ登録テスト
5. 一覧画面でデータ表示確認
6. レポート画面で集計確認

## 7. トラブルシューティング

### よくある問題
- **RLSエラー**: ポリシー設定を確認
- **接続エラー**: 環境変数の形式を確認
- **データが表示されない**: テーブル名・カラム名を確認
- **文字化け**: UTF-8設定を確認
