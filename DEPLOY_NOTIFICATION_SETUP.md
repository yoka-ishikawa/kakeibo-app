# Renderデプロイ通知設定ガイド

## 🔔 概要
Renderでのデプロイ結果（成功・失敗・開始）を自動的にLINEで通知するシステムを構築しました。

## 🛠️ 設定済み機能

### 1. 自動通知機能
- **デプロイ成功**: アプリケーション起動完了時に自動送信
- **デプロイ失敗**: エラー発生時の詳細情報付き通知
- **デプロイ開始**: デプロイプロセス開始時の通知

### 2. 通知エンドポイント
以下のAPIエンドポイントが利用可能です：

```
POST /api/deploy/success   - デプロイ成功通知
POST /api/deploy/failure   - デプロイ失敗通知  
POST /api/deploy/started   - デプロイ開始通知
GET  /api/deploy/test      - テスト通知
```

## 🔧 Render設定手順

### Step 1: 環境変数設定
Renderダッシュボードで以下の環境変数を追加：

```
RENDER_SERVICE_NAME=家計簿アプリ
RENDER_GIT_COMMIT=[自動設定されます]
```

### Step 2: デプロイフック設定（オプション）
Renderのデプロイフック機能を使用して、より詳細な通知を設定可能：

1. **Render Dashboard** → **Service Settings** → **Deploy Hooks**
2. 以下のURLを設定：
   - **On Deploy Start**: `https://[your-app].onrender.com/api/deploy/started`
   - **On Deploy Success**: `https://[your-app].onrender.com/api/deploy/success`
   - **On Deploy Failure**: `https://[your-app].onrender.com/api/deploy/failure`

### Step 3: GitHub Actions連携（推奨）
GitHub Actionsと連携してより詳細な情報を送信：

```yaml
# .github/workflows/notify-deploy.yml
name: Deploy Notification
on:
  push:
    branches: [main]

jobs:
  notify-start:
    runs-on: ubuntu-latest
    steps:
      - name: Notify Deploy Start
        run: |
          curl -X POST https://[your-app].onrender.com/api/deploy/started \
            -H "Content-Type: application/json" \
            -d '{
              "service": "家計簿アプリ",
              "commit": "${{ github.sha }}",
              "branch": "${{ github.ref_name }}",
              "actor": "${{ github.actor }}"
            }'
```

## 📱 通知メッセージの内容

### デプロイ成功通知
- ✅ 成功ステータス
- 📋 サービス名、コミットID、完了時刻
- 🎯 アプリケーションへのリンク

### デプロイ失敗通知  
- ❌ 失敗ステータス
- 🔍 エラー詳細情報
- ⚠️ 対応指示
- 🔗 Renderダッシュボードへのリンク

### デプロイ開始通知
- 🚀 開始ステータス
- 📋 デプロイ情報
- ⏳ 進行状況

## 🧪 テスト方法

### 1. 手動テスト
以下のURLにアクセスしてテスト通知を送信：
```
https://[your-app].onrender.com/api/deploy/test
```

### 2. cURLでのテスト
```bash
# デプロイ成功テスト
curl -X POST https://[your-app].onrender.com/api/deploy/success \
  -H "Content-Type: application/json" \
  -d '{
    "service": "家計簿アプリ（テスト）",
    "commit": "abc1234",
    "timestamp": "2024-12-19T10:30:00"
  }'

# デプロイ失敗テスト
curl -X POST https://[your-app].onrender.com/api/deploy/failure \
  -H "Content-Type: application/json" \
  -d '{
    "service": "家計簿アプリ（テスト）",
    "commit": "abc1234",
    "error": "データベース接続エラー",
    "timestamp": "2024-12-19T10:30:00"
  }'
```

## 🔧 カスタマイズ

### メッセージ内容の変更
`NotificationService.java`の以下のメソッドを編集：
- `createSuccessFlexMessage()` - 成功メッセージ
- `createFailureFlexMessage()` - 失敗メッセージ  
- `createStartFlexMessage()` - 開始メッセージ

### 通知タイミングの調整
`ApplicationStartupListener.java`で起動時通知の条件を変更可能

## 📞 トラブルシューティング

### 通知が送信されない場合
1. **ログ確認**: Renderのログで`LINE通知送信完了`メッセージを確認
2. **環境変数確認**: `SPRING_PROFILES_ACTIVE=production`が設定されているか
3. **ネットワーク確認**: MCP LINE Botサーバーとの接続状況

### エラーメッセージ
- `LINE通知送信エラー`: MCP LINE Botサーバーへの接続失敗
- `通知送信に失敗しました`: JSONパース or 内部エラー

## 🎯 今後の拡張予定
- Slack、Discord通知対応
- デプロイ履歴の記録
- パフォーマンス監視アラート
- 障害復旧通知
