# Render環境変数設定（正しい形式）

## 必須環境変数

### DATABASE_URL
```
jdbc:postgresql://db.gwtjqewcrchqjsywvqjc.supabase.co:5432/postgres?sslmode=require
```

### DB_DRIVER
```
org.postgresql.Driver
```

### DB_USERNAME
```
postgres
```

### DB_PASSWORD
```
141726qooq
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
- SupabaseのPostgreSQLポートは5432
- SSL接続必須のため `sslmode=require` を追加

## 代替設定（接続問題が続く場合）

### DATABASE_URLの代替形式
```
# オプション1: 接続タイムアウト設定付き
jdbc:postgresql://db.gwtjqewcrchqjsywvqjc.supabase.co:5432/postgres?sslmode=require&connectTimeout=10&socketTimeout=10

# オプション2: SSL設定を詳細指定
jdbc:postgresql://db.gwtjqewcrchqjsywvqjc.supabase.co:5432/postgres?sslmode=require&ssl=true&sslcert=&sslkey=&sslrootcert=
```

### トラブルシューティング手順
1. Supabaseダッシュボードで接続情報を再確認
2. パスワードが正しいか確認
3. Supabaseプロジェクトのポーズ状態を確認
4. 必要に応じてSupabaseプロジェクトを再起動

### 一時的な回避策
問題が続く場合は、一時的にH2データベースで動作確認：
```
SPRING_PROFILES_ACTIVE=
```
（productionプロファイルを無効にしてH2使用）
