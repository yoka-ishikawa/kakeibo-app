# Render 環境変数設定の詳細手順

## Render Web Service での環境変数設定

### 1. Render ダッシュボードでの操作手順

1. **https://render.com** にログイン
2. **Dashboard** → **kakeibo-app** (Web Service) をクリック
3. 左メニューの **"Environment"** タブをクリック

### 2. 設定する環境変数一覧

以下の環境変数を一つずつ追加または更新してください：

#### 新規追加または更新が必要な環境変数:

| Key                      | Value                                                                                                           |
| ------------------------ | --------------------------------------------------------------------------------------------------------------- |
| `DATABASE_URL`           | `jdbc:postgresql://dpg-d12hm7je5dus7396aujg-a.singapore-postgres.render.com:5432/tb_info_kanri?sslmode=require` |
| `DB_DRIVER`              | `org.postgresql.Driver`                                                                                         |
| `DB_USERNAME`            | `kakeibo_user`                                                                                                  |
| `DB_PASSWORD`            | `LgjAtV1TaR5Uwot2ecX9fhi5UfDFqek3`                                                                              |
| `JPA_DIALECT`            | `org.hibernate.dialect.PostgreSQLDialect`                                                                       |
| `SPRING_PROFILES_ACTIVE` | `production`                                                                                                    |

### 3. 環境変数の追加・編集方法

#### 新規追加の場合:

1. **"Add Environment Variable"** ボタンをクリック
2. **Key** に変数名を入力（例: `DATABASE_URL`）
3. **Value** に値を入力（例: `jdbc:postgresql://...`）
4. **"Add"** ボタンをクリック

#### 既存の変更の場合:

1. 該当する環境変数の **"Edit"** ボタンをクリック
2. **Value** を新しい値に変更
3. **"Save"** ボタンをクリック

### 4. すべて設定後の確認

1. 全ての環境変数が正しく設定されていることを確認
2. 画面右上の **"Deploy latest commit"** をクリック
3. または自動デプロイが有効な場合は、次の commit/push で自動デプロイされます

### 5. 重要な注意点

- **DATABASE_URL**: 必ず JDBC 形式（`jdbc:postgresql://...`）で始める
- **DB_PASSWORD**: パスワードは間違えないよう慎重にコピー＆ペースト
- **SPRING_PROFILES_ACTIVE**: `production` を設定して PostgreSQL を使用

### 6. 削除が必要な古い環境変数（もしあれば）

以下の Supabase 関連の環境変数があれば削除してください：

- `SUPABASE_URL`
- `SUPABASE_ANON_KEY`
- `SUPABASE_SERVICE_ROLE_KEY`

または古いデータベース設定があれば、上記の新しい値で上書きしてください。
