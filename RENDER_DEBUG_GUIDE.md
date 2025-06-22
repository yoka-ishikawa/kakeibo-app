# Render環境変数緊急設定ガイド

## 🚨 現在の問題
データベース接続が完全に失敗している状況です。すべてのSSL設定での接続が失敗しています。

## 📋 確認すべきRender環境変数

Renderダッシュボードで以下の環境変数が**正確に**設定されているか確認してください：

### DATABASE_URL
```
jdbc:postgresql://dpg-d12hm7je5dus7396aujg-a.singapore-postgres.render.com:5432/tb_info_kanri?sslmode=require
```

### DB_USERNAME
```
kakeibo_user
```
※ログから確認された実際の値です

### DB_PASSWORD
RenderのPostgreSQLダッシュボードから取得した実際のパスワード

### SPRING_PROFILES_ACTIVE
```
production
```

## 🔧 確認手順

1. **Renderダッシュボード**を開く
2. **Environment Variables**タブを選択
3. 上記の値を**完全一致**で設定
4. **Manual Deploy**を実行

## ⚠️ 重要な注意点

- DATABASE_URLは`sslmode=require`を含む完全なURLである必要があります
- DB_USERNAMEは`kakeibo_user`（`tb_info_kanri_user`ではない）
- パスワードはRenderのPostgreSQLページから取得した実際の値を使用

## 🔍 PostgreSQLサーバー確認

もし環境変数が正しく設定されていても接続が失敗する場合：

1. **Render Dashboard → Databases**を開く
2. **PostgreSQLデータベースの状態**を確認
3. **Status**が"Available"になっているか確認
4. **Connection Details**が正しいか確認

## 📞 エスカレーション

上記確認でも解決しない場合、以下の可能性があります：

- Render PostgreSQLサーバーの障害
- ネットワーク接続の問題
- Renderプラットフォーム側の問題

この場合はRender Supportに問い合わせが必要です。
