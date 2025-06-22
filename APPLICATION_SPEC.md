# 🏠 家計簿管理アプリケーション 完全仕様書

## 📋 アプリケーション概要

### プロジェクト基本情報
- **プロジェクト名**: 家計簿管理アプリ (Kakeibo App)
- **目的**: 日本の家計簿文化に基づいた収支管理Webアプリケーション
- **本番URL**: https://kakeibo-app-gy0m.onrender.com
- **開発開始日**: 2025年（推定）
- **現在のバージョン**: 0.0.1-SNAPSHOT

### アプリケーション概要
個人または家族の収入・支出を管理するWebベースの家計簿アプリケーションです。日本語のUI、日本の家計簿文化に合わせたカテゴリ分類、直感的な操作性を重視した設計となっています。

---

## 🏗️ システム・技術仕様

### バックエンド技術スタック
- **プログラミング言語**: Java 21 (OpenJDK)
- **フレームワーク**: Spring Boot 3.5.0
- **ORM**: Spring Data JPA (Hibernate)
- **ビルドツール**: Maven 3.6+
- **アーキテクチャ**: MVC + RESTful API

### フロントエンド技術スタック
- **マークアップ**: HTML5
- **スタイリング**: CSS3
- **スクリプト**: JavaScript (ES6+, Vanilla JS)
- **HTTP通信**: Fetch API (Ajax)
- **アイコン**: Font Awesome

### データベース
- **開発環境**: H2 Database (インメモリ)
- **本番環境**: PostgreSQL (Render PostgreSQL)
- **ORM設定**: Hibernate/JPA

### インフラ・デプロイ
- **ホスティング**: Render (Webサービス)
- **バージョン管理**: GitHub
- **CI/CD**: 自動デプロイ (GitHub → Render)

---

## 🎯 機能要件

### 主要機能一覧

#### 1. 収支登録機能 (`/inexpen.html`)
- **目的**: 日々の収入・支出データの登録
- **機能詳細**:
  - 日付選択（デフォルト：今日）
  - 収支タイプ選択（収入/支出のラジオボタン）
  - カテゴリ選択（動的プルダウン）
  - 金額入力（整数のみ）
  - 備考入力（任意）
  - バリデーション（カテゴリ・金額必須）
  - 編集モード対応（URL パラメータによる既存データ復元）

#### 2. データ一覧機能 (`/list.html`)
- **目的**: 登録済みデータの表示・管理
- **機能詳細**:
  - 全データ一覧表示
  - データの編集（編集画面への遷移）
  - データの削除（確認ダイアログ付き）
  - 日付、種別、カテゴリ、金額の表示

#### 3. レポート機能 (`/report.html`)
- **目的**: 収支の集計・分析・可視化
- **機能詳細**:
  - 月間・年間の収支集計
  - カテゴリ別集計
  - グラフ表示（予定）
  - 収支バランスの表示

#### 4. メニュー機能 (`/index.html`)
- **目的**: アプリケーションのダッシュボード
- **機能詳細**:
  - 各機能への導線
  - 概要表示

### カテゴリ仕様

#### 収入カテゴリ
```javascript
["給料", "おこづかい", "賞与", "臨時収入", "その他"]
```

#### 支出カテゴリ
```javascript
["食事", "日用品", "衣服", "美容", "医療費", "光熱費", "住居費", "通信費", "その他"]
```

---

## 🗄️ データベース設計

### テーブル構造: `tb_info_kanri`

```sql
CREATE TABLE IF NOT EXISTS tb_info_kanri (
    id SERIAL PRIMARY KEY,                              -- 主キー（自動増分）
    user_id VARCHAR(255),                               -- ユーザーID
    syubetu VARCHAR(50) NOT NULL,                       -- 収支種別（"収入"/"支出"）
    kingaku INTEGER NOT NULL,                           -- 金額
    naisyo VARCHAR(255),                                -- 内容・備考
    hiduke DATE NOT NULL,                               -- 日付
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,  -- 作成日時
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP   -- 更新日時
);

-- パフォーマンス向上のためのインデックス
CREATE INDEX IF NOT EXISTS idx_tb_info_kanri_user_id ON tb_info_kanri(user_id);
CREATE INDEX IF NOT EXISTS idx_tb_info_kanri_hiduke ON tb_info_kanri(hiduke);
CREATE INDEX IF NOT EXISTS idx_tb_info_kanri_syubetu ON tb_info_kanri(syubetu);
```

### フィールド詳細
- **id**: 主キー、自動増分
- **user_id**: ユーザー識別子（セッションベース、匿名ユーザー対応）
- **syubetu**: 収支種別（日本語："収入"または"支出"）
- **kingaku**: 金額（整数、負数なし）
- **naisyo**: カテゴリ・備考（日本語カテゴリ名）
- **hiduke**: 取引日付
- **created_at/updated_at**: 監査ログ（自動更新）

---

## 🔌 API 仕様

### RESTful API エンドポイント

#### ベースURL: `/api/infokanri`

#### 1. 収支データ登録
- **エンドポイント**: `POST /api/infokanri`
- **Content-Type**: `application/json`
- **ヘッダー**: `X-User-Id: {userId}`
- **リクエストボディ**:
```json
{
  "hiduke": "2025-06-22",
  "syubetu": "収入",
  "naisyo": "給料",
  "kingaku": 300000
}
```
- **レスポンス**: 登録されたデータ（IDを含む）

#### 2. 全データ取得
- **エンドポイント**: `GET /api/infokanri`
- **レスポンス**: 全収支データの配列

#### 3. データ更新
- **エンドポイント**: `PUT /api/infokanri/{id}`
- **ヘッダー**: `X-User-Id: {userId}`
- **リクエストボディ**: 登録時と同様

#### 4. データ削除
- **エンドポイント**: `DELETE /api/infokanri/{id}`
- **レスポンス**: HTTP 200 (成功) / 404 (未発見)

#### 5. レポートデータ取得
- **エンドポイント**: `GET /api/infokanri/report`
- **クエリパラメータ**: `period`, `startDate`, `endDate`

---

## 🖥️ UI/UX 仕様

### 画面構成
1. **index.html** - メインメニュー・ダッシュボード
2. **inexpen.html** - 収支登録フォーム
3. **list.html** - データ一覧・管理
4. **report.html** - レポート・集計表示

### デザイン方針
- **モバイルファースト**: レスポンシブデザイン
- **日本語UI**: すべて日本語表示
- **直感的操作**: ワンクリック・タップで主要操作
- **視覚的フィードバック**: ボタン状態、ローディング表示

### JavaScript 機能
- **動的フォーム制御**: カテゴリ連動、バリデーション
- **Ajax通信**: Fetch APIによる非同期処理
- **ローカルストレージ**: ユーザーID管理
- **URL パラメータ**: 編集モード対応

---

## ⚙️ 環境・設定

### 開発環境設定
```properties
# application.properties (開発環境)
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver
spring.h2.console.enabled=true
spring.jpa.hibernate.ddl-auto=create-drop
```

### 本番環境設定
```properties
# application-production.properties
spring.datasource.url=${DATABASE_URL}
spring.datasource.driver-class-name=${DB_DRIVER}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.jpa.database-platform=${JPA_DIALECT}
spring.jpa.hibernate.ddl-auto=none
```

### 環境変数（Render）
```bash
DATABASE_URL=jdbc:postgresql://[host]:[port]/[database]?sslmode=require
DB_DRIVER=org.postgresql.Driver
DB_USERNAME=[username]
DB_PASSWORD=[password]
JPA_DIALECT=org.hibernate.dialect.PostgreSQLDialect
SPRING_PROFILES_ACTIVE=production
PORT=10000
```

---

## 🏗️ プロジェクト構造

```
kakeibo-app/
├── src/
│   ├── main/
│   │   ├── java/com/mycompany/webapp/
│   │   │   ├── WebappApplication.java           # メインクラス
│   │   │   ├── controller/
│   │   │   │   └── InfokanriController.java     # REST API
│   │   │   ├── model/
│   │   │   │   └── Infokanri.java              # エンティティ
│   │   │   ├── repository/
│   │   │   │   └── InfokanriRepository.java    # データアクセス
│   │   │   ├── service/
│   │   │   │   └── InfokanriService.java       # ビジネスロジック
│   │   │   └── config/
│   │   │       └── CorsConfig.java             # CORS設定
│   │   └── resources/
│   │       ├── application.properties           # 開発設定
│   │       ├── application-production.properties # 本番設定
│   │       ├── schema.sql                       # DDL
│   │       └── static/                          # フロントエンド
│   │           ├── index.html                   # メニュー
│   │           ├── inexpen.html                 # 登録画面
│   │           ├── list.html                    # 一覧画面
│   │           ├── report.html                  # レポート画面
│   │           ├── css/
│   │           │   ├── style.css               # 共通スタイル
│   │           │   └── report.css              # レポート専用
│   │           └── js/
│   │               ├── inexpen.js              # 登録処理
│   │               ├── list.js                 # 一覧処理
│   │               └── report.js               # レポート処理
│   └── test/
├── pom.xml                                      # Maven設定
├── render.yaml                                  # デプロイ設定
└── README.md                                    # プロジェクト説明
```

---

## 🔧 ビルド・デプロイ

### ローカル開発
```bash
# アプリケーション起動
mvn spring-boot:run

# テスト実行
mvn test

# パッケージビルド
mvn clean package
```

### 本番デプロイ（Render）
```yaml
# render.yaml
services:
  - type: web
    name: kakeibo-app
    env: java
    buildCommand: ./mvnw clean package -DskipTests
    startCommand: java -Dserver.port=$PORT -jar target/webapp-0.0.1-SNAPSHOT.jar
```

---

## 🐛 既知の課題・技術的詳細

### 実装上の特徴
1. **ユーザー管理**: 認証なし、ローカルストレージでセッション管理
2. **データベース**: PostgreSQLとH2の環境別切り替え
3. **フロントエンド**: Vanilla JavaScript（フレームワークなし）
4. **国際化**: 日本語専用（多言語対応なし）

### 潜在的改善点
1. ユーザー認証・セッション管理の強化
2. データベース設計の正規化
3. フロントエンドフレームワーク導入
4. レスポンシブデザインの改善
5. テストカバレッジの向上
6. セキュリティ強化（入力検証、SQL インジェクション対策）

---

## 📝 その他

### 開発・保守に関する注意事項
1. **データベース初期化**: 本番環境では手動でDDL実行が必要
2. **環境変数**: Render等のプラットフォームで適切に設定する必要
3. **CORS設定**: 開発環境では全オリジン許可（本番では制限を検討）
4. **ログ出力**: デバッグ用のSystem.out.println が多用されている

### 想定運用環境
- **個人・家族向け**: 小規模な利用を想定
- **同時接続数**: 低負荷想定
- **データ量**: 中規模（数千〜数万レコード）

---

**この仕様書は現在のアプリケーションの状態を基に作成されており、新規プロジェクト立ち上げ時の参考資料として活用できます。**
