# 家計簿管理アプリ (Kakeibo App)

日本の家計簿文化に基づいた収支管理Webアプリケーションです。

## 🌐 本番環境

**URL**: [https://kakeibo-app-gy0m.onrender.com](https://kakeibo-app-gy0m.onrender.com)

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
- **PostgreSQL** (本番環境 - Supabase)

### フロントエンド
- **HTML5** / **CSS3** / **JavaScript (ES6+)**
- **Font Awesome** (アイコン)
- **Fetch API** (Ajax通信)

### インフラ・デプロイ
- **Render** (Webホスティング)
- **GitHub** (ソースコード管理)
- **GitHub Actions** (CI/CD)

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

本アプリケーションはRenderにデプロイされており、以下の環境変数が設定されています：

```bash
DATABASE_URL=postgresql://username:password@host:5432/database
DB_USERNAME=your_username
DB_PASSWORD=your_password
PORT=8080
```

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
│   │   │   │   └── InfokanriService.java       # ビジネスロジック
│   │   │   └── config/
│   │   │       └── CorsConfig.java             # CORS設定
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
└── README.md
```

## 🎯 設計ドキュメント

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

### デバッグ
- アプリケーションログ: `logging.level.com.mycompany.webapp=DEBUG`
- H2コンソール: `http://localhost:8080/h2-console` (開発環境)

### ビルド・デプロイ
```bash
# 本番ビルド
./mvnw clean package -DskipTests

# Docker実行 (オプション)
docker build -t kakeibo-app .
docker run -p 8080:8080 kakeibo-app
```

## 📈 今後の拡張予定

- [ ] ユーザー認証機能
- [ ] データエクスポート機能
- [ ] 予算管理機能
- [ ] グラフ・チャート表示
- [ ] スマートフォン対応 (PWA)
- [ ] 多言語対応

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

**最終更新**: 2025年6月16日