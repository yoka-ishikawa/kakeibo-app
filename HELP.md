# 家計簿アプリ ヘルプ・FAQ

## 🚀 クイックスタート

### 本番環境
- **URL**: https://kakeibo-app-gy0m.onrender.com
- **ステータス**: 🟢 LIVE運用中
- **自動監視**: 完備・LINE通知対応

### ローカル開発
```bash
# 1. クローン
git clone https://github.com/your-repo/kakeibo-app.git
cd kakeibo-app

# 2. 起動
./mvnw spring-boot:run

# 3. アクセス
http://localhost:8080
```

## 📋 よくある質問

### Q: データが保存されない
**A:** 
- 本番環境: PostgreSQL接続確認（自動診断ログ参照）
- 開発環境: H2データベース（メモリ内）のため再起動時にリセット

### Q: エラーが発生した場合
**A:** 
- 本番環境: 自動監視システムがLINE通知を送信
- 開発環境: コンソールログを確認
- 詳細: `EMERGENCY_RESPONSE_GUIDE.md` 参照

### Q: 機能追加・カスタマイズ
**A:**
- 仕様書: `COMPLETE_SPECIFICATION.md`
- 技術詳細: `README.md`
- 開発指針: プロンプト内 `common-instructions.md`

## 🔧 技術サポート

### 主要コンポーネント
- **Backend**: Spring Boot 3.5.0 + Java 21
- **Database**: PostgreSQL (本番) / H2 (開発)
- **Frontend**: HTML5 + CSS3 + JavaScript
- **Infrastructure**: Render Platform
- **Monitoring**: 自動監視・LINE通知システム

### 開発ツール
- **VS Code**: MCP Server連携でRender API直接操作
- **Maven**: `./mvnw` でビルド・テスト・実行
- **Git**: 自動デプロイ（GitHubプッシュ連動）

### 参考リンク
- [Spring Boot Documentation](https://docs.spring.io/spring-boot/)
- [Render Platform Documentation](https://render.com/docs)
- [Maven Documentation](https://maven.apache.org/guides/)

