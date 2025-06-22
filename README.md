# 家計簿管理アプリ (Kakeibo App)

日本の家計簿文化に基づいた収支管理Webアプリケーションです。Render環境での本格運用と自動監視・通知システムを搭載しています。

## 🌐 本番環境

**URL**: [https://kakeibo-app-gy0m.onrender.com](https://kakeibo-app-gy0m.onrender.com)  
**ステータス**: 🟢 LIVE運用中  
**最終デプロイ**: 2025年1月26日  
**監視システム**: 自動デプロイ監視・LINE通知完備

## 📋 機能一覧

### 主要機能
- **収支登録**: 収入・支出の登録とカテゴリ分類
- **データ一覧**: 登録したデータの一覧表示・編集・削除
- **レポート**: 月間・年間の収支集計とグラフ表示
- **CRUD操作**: 完全なデータ管理機能

### 画面構成
1. **メニュー画面** (`index.html`) - ダッシュボード
2. **収支登録画面** (`inexpen.html`) - データ入力フォーム
3. **収支一覧画面** (`list.html`) - データ管理
4. **レポート画面** (`report.html`) - 集計・分析

## 🛠 技術スタック

### バックエンド
- **Java 21** (OpenJDK)
- **Spring Boot 3.5.0**
- **Spring Data JPA** (Hibernate)
- **Maven** (ビルドツール)

### データベース
- **H2 Database** (開発環境)
- **PostgreSQL** (本番環境 - Render PostgreSQL)
- **HikariCP** (コネクションプール)
- **JDBC URL自動変換** (認証情報エラー対策)

### フロントエンド
- **HTML5** / **CSS3** / **JavaScript (ES6+)**
- **Font Awesome** (アイコン)
- **Fetch API** (Ajax通信)

### インフラ・デプロイ
- **Render** (Webホスティング・PostgreSQL)
- **GitHub** (ソースコード管理・自動デプロイ)
- **Render MCP Server** (VS Code統合・API監視)

### 運用・監視システム  
- **自動デプロイ監視** (Render API連携)
- **LINE通知システム** (デプロイ成功/失敗/エラー通知)
- **データベース接続診断** (多段階エラー解析)
- **システム状態自動通知** (Flex メッセージ形式)

## 🚀 セットアップ

### 前提条件
- Java 21以上
- Maven 3.6以上
- Git

### ローカル開発環境

1. **リポジトリのクローン**
```bash
git clone https://github.com/yoka-ishikawa/kakeibo-app.git
cd kakeibo-app
```

2. **依存関係のインストール**
```bash
./mvnw clean install
```

3. **アプリケーションの起動**
```bash
./mvnw spring-boot:run
```

4. **ブラウザでアクセス**
```
http://localhost:8080
```

### 本番環境デプロイ

本アプリケーションはRenderにデプロイされており、自動監視・通知システムが稼働しています：

#### 環境変数設定
```bash
DATABASE_URL=postgresql://username:password@host:5432/database
DB_USERNAME=your_username
DB_PASSWORD=your_password
PORT=8080
RENDER_SERVICE_NAME=kakeibo-app
SPRING_PROFILES_ACTIVE=production
```

#### 自動監視・通知機能
- **デプロイ監視**: Render API経由でリアルタイム監視
- **LINE通知**: デプロイ成功/失敗/エラー時に自動通知
- **データベース診断**: 接続エラーの詳細分析・解決ガイド
- **MCP連携**: VS CodeからRender APIを直接操作可能

## 📊 API仕様

### REST API エンドポイント

| メソッド | エンドポイント | 説明 |
|---------|-------------|------|
| `GET` | `/api/infokanri` | 全データ取得 |
| `POST` | `/api/infokanri` | 新規データ登録 |
| `PUT` | `/api/infokanri/{id}` | データ更新 |
| `DELETE` | `/api/infokanri/{id}` | データ削除 |
| `GET` | `/api/infokanri/report` | レポートデータ取得 |

### リクエスト例

**新規登録 (POST)**
```json
{
  "type": "income",
  "category": "給与",
  "amount": 300000,
  "registeredAt": "2025-06-16",
  "updateDateTime": "2025-06-16T06:30:00.000Z"
}
```

**レスポンス例**
```json
{
  "id": 1,
  "userToken": "user-token",
  "type": "income",
  "category": "給与",
  "amount": 300000,
  "registeredAt": "2025-06-16",
  "updateDateTime": null
}
```

## 📁 プロジェクト構成

```
kakeibo-app/
├── src/
│   ├── main/
│   │   ├── java/com/mycompany/webapp/
│   │   │   ├── WebappApplication.java          # メインクラス
│   │   │   ├── controller/
│   │   │   │   ├── InfokanriController.java    # REST API
│   │   │   │   └── UserTokenController.java    # 認証API
│   │   │   ├── model/
│   │   │   │   └── Infokanri.java              # エンティティ
│   │   │   ├── repository/
│   │   │   │   └── InfokanriRepository.java    # データアクセス
│   │   │   ├── service/
│   │   │   │   ├── InfokanriService.java       # ビジネスロジック
│   │   │   │   ├── NotificationService.java    # LINE通知サービス
│   │   │   │   └── RenderMonitoringService.java # 自動監視サービス
│   │   │   └── config/
│   │   │       ├── CorsConfig.java             # CORS設定
│   │   │       └── DatabaseConfig.java         # DB設定(高度診断付き)
│   │   └── resources/
│   │       ├── static/                         # 静的リソース
│   │       │   ├── css/
│   │       │   ├── js/
│   │       │   └── *.html
│   │       ├── application.properties          # 開発設定
│   │       └── application-production.properties # 本番設定
│   └── test/                                   # テストコード
├── pom.xml                                     # Maven設定
├── render.yaml                                 # Render設定
├── Dockerfile                                  # コンテナ設定
├── COMPLETE_SPECIFICATION.md                   # 完全仕様書
├── DEPLOY_NOTIFICATION_SETUP.md               # デプロイ通知設定
├── EMERGENCY_RESPONSE_GUIDE.md                # 緊急対応ガイド
└── README.md                                  # このファイル
```

## 🎯 設計ドキュメント

### 主要技術文書
- **[完全仕様書](COMPLETE_SPECIFICATION.md)** - システム全体の詳細仕様
- **[デプロイ通知設定](DEPLOY_NOTIFICATION_SETUP.md)** - 自動監視・LINE通知設定
- **[緊急対応ガイド](EMERGENCY_RESPONSE_GUIDE.md)** - 障害発生時の対応手順
- **[Render環境設定](render-env-config.md)** - 本番環境設定詳細

### 外部設計資料  
- [要件定義](https://docs.google.com/spreadsheets/d/1Zykgngd74m_7o4Q2fOFoc-ADYFo8yUvBVfWy_AblrVE/edit?usp=sharing)
- [基本設計 - メニュー画面](https://1drv.ms/x/c/58cea19fddb0da42/EctnvARHl_1FmGneeVdNnLIBMI-n8h4nmD3XnTzzbs-xnA?e=PEHUKN)
- [基本設計 - 収支登録画面](https://1drv.ms/x/c/58cea19fddb0da42/EVyvpGkGaGVGp8XeeQBbI7kBVDU4-XgAOXP1F1zyWZVlBA?e=N2oalV)
- [基本設計 - 一覧画面](https://1drv.ms/x/c/58cea19fddb0da42/EaIhIxqI35xFuMFjpUAkNCMBfga0_g3ZKbE1hzxDb1g_vA?e=QgpCA5)
- [基本設計 - レポート画面](https://1drv.ms/x/c/58cea19fddb0da42/EUL2JbgW-OdMl7ATgPu4gj4B6MTztEyBz1lGiEzeMQGz9A?e=gy3KWi)
- [WBS](https://docs.google.com/spreadsheets/d/1NNerjxjCcL9oa6iLdykSz05dcb6O8V50o1Q_8gNreIo/edit?gid=0#gid=0)

## 🧪 テスト

```bash
# 単体テスト実行
./mvnw test

# カバレッジレポート生成
./mvnw test jacoco:report
```

## 🔧 開発者向け情報

### コーディング規約
- Java: Spring Boot規約に準拠
- JavaScript: ES6+モダン構文使用
- CSS: BEM命名規則推奨

### デバッグ・ログ
- アプリケーションログ: `logging.level.com.mycompany.webapp=DEBUG`
- H2コンソール: `http://localhost:8080/h2-console` (開発環境)
- Render監視ログ: `RenderMonitoringService` 自動出力
- データベース診断: `DatabaseConfig` 詳細分析ログ

### ビルド・デプロイ
```bash
# 本番ビルド
./mvnw clean package -DskipTests

# Docker実行 (オプション)
docker build -t kakeibo-app .
docker run -p 8080:8080 kakeibo-app

# Render MCP操作 (VS Code内)
# サービスリスト取得、デプロイ監視、ログ確認が可能
```

### 監視・運用
- **Render MCP Server**: VS CodeからRender APIを直接操作
- **自動通知**: デプロイ結果・エラー発生時にLINE通知
- **データベース診断**: JDBC接続エラーの自動解析・解決提案
- **ログ監視**: デプロイログ・アプリケーションログの自動取得

## 📈 今後の拡張予定

### 完了済み機能 ✅
- [x] Render本番環境の安定運用
- [x] PostgreSQL接続エラー自動解決システム
- [x] デプロイ監視・自動通知システム(LINE)
- [x] VS Code + MCP Serverによる運用自動化
- [x] データベース接続診断・エラー分析システム

### 開発予定 🚧
- [ ] ユーザー認証機能
- [ ] データエクスポート機能
- [ ] 予算管理機能
- [ ] グラフ・チャート表示の強化
- [ ] スマートフォン対応 (PWA)
- [ ] 多言語対応
- [ ] 高度な分析・レポート機能

## 🤝 コントリビューション

1. フォークする
2. フィーチャーブランチを作成 (`git checkout -b feature/amazing-feature`)
3. 変更をコミット (`git commit -m 'Add amazing feature'`)
4. ブランチにプッシュ (`git push origin feature/amazing-feature`)
5. プルリクエストを作成

## 📄 ライセンス

このプロジェクトは個人開発のデモアプリケーションです。

## 👨‍💻 開発者

- **Yoka Ishikawa** - *Initial work*

---

**最終更新**: 2025年6月23日  
**プロジェクト状況**: 🟢 本番稼働中・自動監視システム完備  
**技術レベル**: エンタープライズ級・本格運用対応