# Render環境変数設定（Render PostgreSQL用）

## 必須環境変数

### DATABASE_URL
```
jdbc:postgresql://dpg-d1j8h7he5dus7396aujg-a.singapore-postgres.render.com:5432/kakeibo_db?sslmode=require
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

## 設定手順

1. Renderダッシュボードでサービスを選択
2. Environment タブを開く
3. 上記の環境変数を一つずつ追加/修正
4. Deploy latest commit をクリックして再デプロイ

## 注意点

- DATABASE_URLには認証情報（ユーザー名・パスワード）を含めない
- 認証情報は別途DB_USERNAME, DB_PASSWORDで設定
- Render PostgreSQLのポートは5432
- SSL接続必須のため `sslmode=require` を追加

## Render PostgreSQLでのテーブル作成

Renderダッシュボードから以下の手順でテーブルを作成：

1. **データベース接続**
   - Renderダッシュボード → `kakeibo-db` → Connect
   - またはPSQLコマンドを使用:
   ```bash
   PGPASSWORD=LgjAtV1TaR5Uwot2ecX9fhi5UfDFqek3 psql -h dpg-d12hm7je5dus7396aujg-a.singapore-postgres.render.com -U kakeibo_user tb_info_kanri
   ```

2. **テーブル作成SQL実行**
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

### DATABASE_URLの代替形式
```
# オプション1: 接続タイムアウト設定付き
jdbc:postgresql://dpg-d12hm7je5dus7396aujg-a.singapore-postgres.render.com:5432/tb_info_kanri?sslmode=require&connectTimeout=10&socketTimeout=10

# オプション2: SSL設定を詳細指定
jdbc:postgresql://dpg-d12hm7je5dus7396aujg-a.singapore-postgres.render.com:5432/tb_info_kanri?sslmode=require&ssl=true
```

### トラブルシューティング手順
1. Renderダッシュボードで接続情報を再確認
2. パスワードが正しいか確認
3. Renderデータベースの状態を確認
4. 必要に応じてRenderサービスを再起動

### 一時的な回避策
問題が続く場合は、一時的にH2データベースで動作確認：
```
SPRING_PROFILES_ACTIVE=
```
（productionプロファイルを無効にしてH2使用）
