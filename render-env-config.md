# Render 環境変数設定（Render PostgreSQL 用）

## 必須環境変数

### DATABASE_URL

```
jdbc:postgresql://dpg-d1j8h7he5dus7396aujg-a.singapore-postgres.render.com:5432/kakeibo_db?sslmode=require&connectTimeout=30&socketTimeout=30&loginTimeout=30
```

### DB_DRIVER

```
org.postgresql.Driver
```

### DB_USERNAME

```
kakeibo_user
```

### DB_PASSWORD

```
LgjAtV1TaR5Uwot2ecX9fhi5UfDFqek3
```

### JPA_DIALECT

```
org.hibernate.dialect.PostgreSQLDialect
```

### SPRING_PROFILES_ACTIVE

```
production
```

### PORT

```
10000
```

### DDL_AUTO

```
none
```

### SHOW_SQL

```
true
```

### SQL_INIT_MODE

```
never
```

## 設定手順

1. Render ダッシュボードでサービスを選択
2. Environment タブを開く
3. 上記の環境変数を一つずつ追加/修正
4. Deploy latest commit をクリックして再デプロイ

## 注意点

- DATABASE_URL には認証情報（ユーザー名・パスワード）を含めない
- 認証情報は別途 DB_USERNAME, DB_PASSWORD で設定
- Render PostgreSQL のポートは 5432
- SSL 接続必須のため `sslmode=require` を追加

## Render PostgreSQL でのテーブル作成

Render ダッシュボードから以下の手順でテーブルを作成：

1. **データベース接続**

   - Render ダッシュボード → `kakeibo-db` → Connect
   - または PSQL コマンドを使用:

   ```bash
   PGPASSWORD=LgjAtV1TaR5Uwot2ecX9fhi5UfDFqek3 psql -h dpg-d12hm7je5dus7396aujg-a.singapore-postgres.render.com -U kakeibo_user tb_info_kanri
   ```

2. **テーブル作成 SQL 実行**

   ```sql
   -- 家計簿情報管理テーブル
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

   -- インデックス作成（パフォーマンス向上のため）
   CREATE INDEX IF NOT EXISTS idx_tb_info_kanri_user_id ON tb_info_kanri(user_id);
   CREATE INDEX IF NOT EXISTS idx_tb_info_kanri_hiduke ON tb_info_kanri(hiduke);
   CREATE INDEX IF NOT EXISTS idx_tb_info_kanri_syubetu ON tb_info_kanri(syubetu);
   ```

## 代替設定（接続問題が続く場合）

### DATABASE_URL の代替形式

```
# オプション1: 接続タイムアウト設定付き
jdbc:postgresql://dpg-d12hm7je5dus7396aujg-a.singapore-postgres.render.com:5432/tb_info_kanri?sslmode=require&connectTimeout=10&socketTimeout=10

# オプション2: SSL設定を詳細指定
jdbc:postgresql://dpg-d12hm7je5dus7396aujg-a.singapore-postgres.render.com:5432/tb_info_kanri?sslmode=require&ssl=true
```

### トラブルシューティング手順

1. Render ダッシュボードで接続情報を再確認
2. パスワードが正しいか確認
3. Render データベースの状態を確認
4. 必要に応じて Render サービスを再起動

### 一時的な回避策

問題が続く場合は、一時的に H2 データベースで動作確認：

```
SPRING_PROFILES_ACTIVE=
```

（production プロファイルを無効にして H2 使用）

## 接続トラブルシューティング用設定

### 接続診断のための一時的な環境変数

**注意: 接続確認後は削除すること**

### LOGGING_LEVEL_COM_ZAXXER_HIKARI

```
DEBUG
```

### LOGGING_LEVEL_ORG_POSTGRESQL

```
DEBUG
```

### SPRING_JPA_SHOW_SQL

```
true
```

## 接続エラー対応手順

1. **環境変数の確認**

   - 上記すべての環境変数が正しく設定されているか確認
   - 特に DATABASE_URL, DB_USERNAME, DB_PASSWORD の値をチェック

2. **Render PostgreSQL サービスの状態確認**

   - Render ダッシュボードで`kakeibo-db`サービスが"Available"状態か確認
   - 接続情報が変更されていないか確認

3. **段階的デプロイ**

   - まず接続のみテスト（schema.sql は無効）
   - 接続成功後、手動でテーブル作成
   - 最後にアプリケーション機能をテスト

4. **接続情報の再確認**
   - Render の PostgreSQL ダッシュボードで最新の接続情報を確認
   - 接続文字列、ユーザー名、パスワードが正確かチェック
