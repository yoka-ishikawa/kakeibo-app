# 🏠 家計簿管理アプリケーション 最新完全仕様書

## 📋 プロジェクト概要

### 基本情報
- **プロジェクト名**: 家計簿管理アプリ (Kakeibo Management App)
- **用途**: 個人・家族向け収支管理Webアプリケーション
- **開発言語**: Java + JavaScript
- **フレームワーク**: Spring Boot 3.5.0
- **データベース**: PostgreSQL (本番) / H2 (開発)
- **ホスティング**: Render Platform
- **本番URL**: https://kakeibo-app-gy0m.onrender.com
- **バージョン**: 0.0.1-SNAPSHOT

### アプリケーションの特徴
- **日本語完全対応**: UI、データ、カテゴリすべて日本語
- **シンプル設計**: ユーザー認証なし、直感的な操作
- **レスポンシブ対応**: PC・スマートフォン両対応
- **リアルタイム更新**: Ajax通信による動的画面更新

---

## 🏗️ システムアーキテクチャ

### 技術スタック詳細

#### バックエンド
```
Java 21 (OpenJDK LTS)
├── Spring Boot 3.5.0
│   ├── Spring Web (REST API)
│   ├── Spring Data JPA (ORM)
│   ├── Spring Boot Starter Test
│   └── Spring Boot Starter Thymeleaf
├── PostgreSQL Driver
├── H2 Database (開発用)
└── Maven 3.6+ (ビルド管理)
```

#### フロントエンド
```
HTML5 + CSS3 + Vanilla JavaScript
├── Font Awesome 6.0.0 (アイコン)
├── Fetch API (HTTP通信)
├── LocalStorage (セッション管理)
└── レスポンシブデザイン
```

#### インフラ・DevOps
```
Render Platform
├── Web Service (アプリケーション)
├── PostgreSQL Database
├── 自動デプロイ (GitHub連携)
└── 環境変数管理
```

### アーキテクチャパターン
- **MVC + REST API**: 標準的な3層アーキテクチャ
- **Repository Pattern**: データアクセス層の抽象化
- **DTO/Entity分離**: データ転送オブジェクトの活用
- **依存性注入**: Spring DIコンテナ活用

---

## 🎯 機能仕様

### 1. メインメニュー機能 (`index.html`)

#### 画面概要
- アプリケーションのエントリーポイント
- 各機能への導線を提供

#### 機能詳細
```html
<!-- 主要ナビゲーション -->
- 収支登録ボタン (→ /inexpen.html)
- 収支登録一覧ボタン (→ /list.html)
- レポートボタン (→ /report.html)
```

### 2. 収支登録機能 (`inexpen.html`)

#### 画面概要
- 日々の収入・支出データの登録・編集
- 直感的なフォーム設計

#### フォーム項目
| 項目 | 型 | 必須 | 説明 |
|------|-----|-----|------|
| 登録年月日 | date | ✓ | デフォルト：今日 |
| 収支タイプ | radio | ✓ | 収入/支出 |
| カテゴリ | select | ✓ | 収支タイプに連動 |
| 金額 | number | ✓ | 正の整数のみ |
| 備考 | text | - | 任意入力 |

#### カテゴリ定義
```javascript
// 収入カテゴリ
const incomeCategories = [
  "給料", "おこづかい", "賞与", "臨時収入", "その他"
];

// 支出カテゴリ  
const expenditureCategories = [
  "食事", "日用品", "衣服", "美容", "医療費", 
  "光熱費", "住居費", "通信費", "その他"
];
```

#### 機能詳細
- **動的カテゴリ変更**: 収支タイプ変更時にカテゴリ自動更新
- **リアルタイムバリデーション**: 必須項目チェック
- **編集モード対応**: URLパラメータから既存データ復元
- **ユーザーセッション**: LocalStorage でユーザーID管理

### 3. データ一覧機能 (`list.html`)

#### 画面概要
- 登録済みデータの一覧表示・管理
- 編集・削除操作を提供

#### 表示項目
| カラム | データ型 | 表示形式 |
|--------|----------|----------|
| 登録年月日 | DATE | YYYY-MM-DD |
| 収支 | VARCHAR | 収入/支出 |
| カテゴリ | VARCHAR | 日本語カテゴリ名 |
| 金額 | INTEGER | ¥ 999,999 形式 |
| 操作 | - | 編集・削除ボタン |

#### 機能詳細
- **全データ表示**: 時系列順（最新優先）
- **編集機能**: 既存データを登録画面で編集
- **削除機能**: 確認ダイアログ付き削除
- **空データ対応**: "データがありません"メッセージ

### 4. レポート機能 (`report.html`)

#### 画面概要
- 収支データの集計・分析・可視化
- 期間別・カテゴリ別の統計表示

#### 機能詳細
- **期間選択**: 月間・年間切り替え
- **収支集計**: 収入・支出・収支バランス
- **カテゴリ別集計**: 支出カテゴリ別内訳
- **視覚的表示**: 金額の強調表示・色分け

---

## 🗄️ データベース設計

### テーブル: `tb_info_kanri`

```sql
-- 家計簿情報管理テーブル
CREATE TABLE IF NOT EXISTS tb_info_kanri (
    id SERIAL PRIMARY KEY,                          -- 主キー
    user_id VARCHAR(255),                           -- ユーザーID
    syubetu VARCHAR(50) NOT NULL,                   -- 収支種別
    kingaku INTEGER NOT NULL,                       -- 金額
    naisyo VARCHAR(255),                            -- 内容・カテゴリ
    hiduke DATE NOT NULL,                           -- 日付
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- パフォーマンス最適化インデックス
CREATE INDEX IF NOT EXISTS idx_tb_info_kanri_user_id ON tb_info_kanri(user_id);
CREATE INDEX IF NOT EXISTS idx_tb_info_kanri_hiduke ON tb_info_kanri(hiduke);
CREATE INDEX IF NOT EXISTS idx_tb_info_kanri_syubetu ON tb_info_kanri(syubetu);
```

### フィールド仕様

| フィールド | 型 | 制約 | 説明 |
|-----------|-----|-----|------|
| id | SERIAL | PRIMARY KEY | 自動増分主キー |
| user_id | VARCHAR(255) | - | ユーザー識別子 |
| syubetu | VARCHAR(50) | NOT NULL | 収支種別（"収入"/"支出"） |
| kingaku | INTEGER | NOT NULL | 金額（正の整数） |
| naisyo | VARCHAR(255) | - | カテゴリ・備考 |
| hiduke | DATE | NOT NULL | 取引日付 |
| created_at | TIMESTAMP | DEFAULT NOW() | 作成日時 |
| updated_at | TIMESTAMP | DEFAULT NOW() | 更新日時 |

### データ例
```sql
INSERT INTO tb_info_kanri (user_id, syubetu, kingaku, naisyo, hiduke) VALUES
('user123', '収入', 300000, '給料', '2025-06-20'),
('user123', '支出', 15000, '食事', '2025-06-21'),
('user123', '支出', 3000, '日用品', '2025-06-22');
```

---

## 🔌 REST API 仕様

### ベースURL: `/api/infokanri`

#### 1. 収支データ作成
```http
POST /api/infokanri
Content-Type: application/json
X-User-Id: user123

{
  "hiduke": "2025-06-22",
  "syubetu": "収入", 
  "naisyo": "給料",
  "kingaku": 300000
}
```

**レスポンス**
```json
{
  "id": 1,
  "userId": "user123",
  "hiduke": "2025-06-22",
  "syubetu": "収入",
  "naisyo": "給料", 
  "kingaku": 300000,
  "createdAt": "2025-06-22T10:30:00Z",
  "updatedAt": "2025-06-22T10:30:00Z"
}
```

#### 2. 全データ取得
```http
GET /api/infokanri
```

**レスポンス**
```json
[
  {
    "id": 1,
    "userId": "user123",
    "hiduke": "2025-06-22",
    "syubetu": "収入",
    "naisyo": "給料",
    "kingaku": 300000,
    "createdAt": "2025-06-22T10:30:00Z",
    "updatedAt": "2025-06-22T10:30:00Z"
  }
]
```

#### 3. データ更新
```http
PUT /api/infokanri/{id}
Content-Type: application/json
X-User-Id: user123

{
  "hiduke": "2025-06-22",
  "syubetu": "支出",
  "naisyo": "食事", 
  "kingaku": 2000
}
```

#### 4. データ削除
```http
DELETE /api/infokanri/{id}
```

#### 5. レポートデータ取得
```http
GET /api/infokanri/report?period=monthly&startDate=2025-06-01&endDate=2025-06-30
```

---

## 💻 フロントエンド実装詳細

### JavaScript モジュール構成

#### 1. 共通機能 (`common.js`)
```javascript
// ユーザートークン管理
function initializeUserToken() {
  if (!localStorage.getItem("userToken")) {
    localStorage.setItem("userToken", "user_" + Date.now());
  }
}

// 日付ユーティリティ
function formatDate(date) {
  return date.toISOString().split('T')[0];
}

// 金額フォーマット
function formatCurrency(amount) {
  return new Intl.NumberFormat('ja-JP', {
    style: 'currency',
    currency: 'JPY'
  }).format(amount);
}
```

#### 2. 登録画面 (`inexpen.js`)
```javascript
// フォーム制御
function updateDropdown(type) {
  const categories = type === 'income' ? 
    incomeCategories : expenditureCategories;
  updateCategorySelect(categories);
}

// バリデーション
function validateForm() {
  const required = ['category', 'amount'];
  return required.every(field => 
    document.getElementById(field).value.trim() !== ''
  );
}

// データ送信
async function submitForm(data) {
  const response = await fetch('/api/infokanri', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'X-User-Id': localStorage.getItem('userToken')
    },
    body: JSON.stringify(data)
  });
  return response.json();
}
```

#### 3. 一覧画面 (`list.js`)
```javascript
// データ表示
async function loadDataList() {
  const response = await fetch('/api/infokanri');
  const data = await response.json();
  renderTable(data);
}

// テーブル描画
function renderTable(data) {
  const tbody = document.querySelector('tbody');
  tbody.innerHTML = data.map(item => `
    <tr data-id="${item.id}">
      <td>${item.hiduke}</td>
      <td>${item.syubetu}</td>
      <td>${item.naisyo}</td>
      <td>${formatCurrency(item.kingaku)}</td>
      <td>
        <button onclick="editItem(${item.id})">編集</button>
        <button onclick="deleteItem(${item.id})">削除</button>
      </td>
    </tr>
  `).join('');
}
```

#### 4. レポート画面 (`report.js`)
```javascript
// 集計計算
function calculateTotals(data) {
  return data.reduce((acc, item) => {
    if (item.syubetu === '収入') {
      acc.income += item.kingaku;
    } else {
      acc.expenditure += item.kingaku;
    }
    return acc;
  }, { income: 0, expenditure: 0 });
}

// レポート表示
function displayReport(data) {
  const totals = calculateTotals(data);
  const balance = totals.income - totals.expenditure;
  
  document.querySelector('.income').textContent = 
    `収入: ${formatCurrency(totals.income)}`;
  document.querySelector('.expenditure').textContent = 
    `支出: ${formatCurrency(totals.expenditure)}`;
  document.querySelector('.balance').textContent = 
    `収支: ${formatCurrency(balance)}`;
}
```

---

## ⚙️ 環境・設定

### 開発環境設定

#### `application.properties` (開発環境)
```properties
# アプリケーション名
spring.application.name=webapp

# H2データベース設定
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# H2コンソール有効化
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# JPA設定
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# SQL初期化
spring.sql.init.mode=always
spring.sql.init.continue-on-error=true
```

#### `application-production.properties` (本番環境)
```properties
# アプリケーション名
spring.application.name=webapp

# PostgreSQL設定
spring.datasource.url=${DATABASE_URL}
spring.datasource.driver-class-name=${DB_DRIVER}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

# JPA設定
spring.jpa.database-platform=${JPA_DIALECT}
spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# 接続プール設定
spring.datasource.hikari.maximum-pool-size=3
spring.datasource.hikari.minimum-idle=1
spring.datasource.hikari.connection-timeout=30000

# SQL初期化設定
spring.sql.init.mode=never
spring.sql.init.continue-on-error=true

# Jackson設定（JSONシリアライゼーション対応）
spring.jackson.serialization.write-dates-as-timestamps=false
spring.jackson.time-zone=Asia/Tokyo
```

### 環境変数設定

#### Render Platform環境変数
```bash
# データベース接続
DATABASE_URL=jdbc:postgresql://dpg-xxx-a.singapore-postgres.render.com:5432/kakeibo_db_xxx?sslmode=require
DB_DRIVER=org.postgresql.Driver
DB_USERNAME=kakeibo_db_xxx_user
DB_PASSWORD=xxx
JPA_DIALECT=org.hibernate.dialect.PostgreSQLDialect

# Spring Boot設定
SPRING_PROFILES_ACTIVE=production
PORT=10000

# Java設定
JAVA_TOOL_OPTIONS=-XX:MaxRAMPercentage=50.0
```

---

## 🏗️ プロジェクト構造

```
kakeibo-app/
├── src/
│   ├── main/
│   │   ├── java/com/mycompany/webapp/
│   │   │   ├── WebappApplication.java           # Spring Boot メインクラス
│   │   │   ├── controller/
│   │   │   │   ├── InfokanriController.java     # REST API コントローラー
│   │   │   │   └── UserTokenController.java     # ユーザートークン管理
│   │   │   ├── model/
│   │   │   │   └── Infokanri.java              # JPA エンティティ
│   │   │   ├── repository/
│   │   │   │   └── InfokanriRepository.java    # Spring Data JPA リポジトリ
│   │   │   ├── service/
│   │   │   │   └── InfokanriService.java       # ビジネスロジック層
│   │   │   └── config/
│   │   │       └── CorsConfig.java             # CORS設定
│   │   └── resources/
│   │       ├── application.properties           # 開発環境設定
│   │       ├── application-production.properties # 本番環境設定
│   │       ├── schema.sql                       # DDL スクリプト
│   │       └── static/                          # 静的コンテンツ
│   │           ├── index.html                   # メインメニュー
│   │           ├── inexpen.html                 # 収支登録画面
│   │           ├── list.html                    # データ一覧画面
│   │           ├── report.html                  # レポート画面
│   │           ├── css/
│   │           │   ├── style.css               # 共通スタイル
│   │           │   └── report.css              # レポート専用スタイル
│   │           └── js/
│   │               ├── inexpen.js              # 登録画面ロジック
│   │               ├── list.js                 # 一覧画面ロジック
│   │               └── report.js               # レポート画面ロジック
│   └── test/
│       └── java/com/mycompany/webapp/
│           ├── WebappApplicationTests.java      # 統合テスト
│           ├── RenderPostgreSQLConnectionTest.java # DB接続テスト
│           └── SimpleConnectionTest.java        # 簡易接続テスト
├── pom.xml                                      # Maven設定
├── render.yaml                                  # Render デプロイ設定
├── README.md                                    # プロジェクト概要
├── APPLICATION_SPEC.md                          # 旧仕様書
└── COMPLETE_SPECIFICATION.md                    # 本仕様書
```

---

## 🚀 ビルド・デプロイ

### ローカル開発手順

#### 1. 環境準備
```bash
# 必要な環境
- Java 21 (OpenJDK)
- Maven 3.6+
- Git

# プロジェクトクローン
git clone https://github.com/your-repo/kakeibo-app.git
cd kakeibo-app
```

#### 2. ローカル実行
```bash
# 依存関係インストール・コンパイル
mvn clean compile

# テスト実行
mvn test

# アプリケーション起動
mvn spring-boot:run

# アクセス
http://localhost:8080
```

#### 3. H2データベース確認
```bash
# H2コンソールアクセス
http://localhost:8080/h2-console

# 接続設定
JDBC URL: jdbc:h2:mem:testdb
User Name: sa
Password: (空文字)
```

### 本番デプロイ手順

#### 1. Render設定 (`render.yaml`)
```yaml
services:
  - type: web
    name: kakeibo-app
    env: java
    buildCommand: ./mvnw clean package -DskipTests
    startCommand: java -Dserver.port=$PORT -jar target/webapp-0.0.1-SNAPSHOT.jar
    envVars:
      - key: JAVA_TOOL_OPTIONS
        value: -XX:MaxRAMPercentage=50.0
      - key: SPRING_PROFILES_ACTIVE
        value: production
```

#### 2. データベース初期化
```sql
-- Render PostgreSQL Webエディタまたはpsqlクライアントで実行
-- schema.sql の内容をコピー＆ペーストで実行
```

#### 3. 環境変数設定
```bash
# Render Dashboard > Environment Variables
DATABASE_URL=postgresql://...
DB_DRIVER=org.postgresql.Driver
DB_USERNAME=...
DB_PASSWORD=...
JPA_DIALECT=org.hibernate.dialect.PostgreSQLDialect
SPRING_PROFILES_ACTIVE=production
```

#### 4. 自動デプロイ
- GitHub へのプッシュで自動デプロイ
- Render Dashboard でビルドログ確認
- 本番URL でアプリケーション確認

---

## 🔍 テスト戦略

### テストの種類

#### 1. 単体テスト
```java
@ExtendWith(MockitoExtension.class)
class InfokanriServiceTest {
    
    @Mock
    private InfokanriRepository repository;
    
    @InjectMocks
    private InfokanriService service;
    
    @Test
    void testSaveInfokanri() {
        // Given
        Infokanri input = new Infokanri();
        input.setSyubetu("収入");
        input.setKingaku(100000);
        
        // When
        Infokanri result = service.saveInfokanri(input);
        
        // Then
        assertThat(result.getSyubetu()).isEqualTo("収入");
        assertThat(result.getKingaku()).isEqualTo(100000);
    }
}
```

#### 2. 統合テスト
```java
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class InfokanriControllerIntegrationTest {
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Test
    void testCreateInfokanri() {
        // Given
        Infokanri infokanri = new Infokanri();
        infokanri.setSyubetu("収入");
        infokanri.setKingaku(100000);
        
        // When
        ResponseEntity<Infokanri> response = restTemplate.postForEntity(
            "/api/infokanri", infokanri, Infokanri.class);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getId()).isNotNull();
    }
}
```

#### 3. E2Eテスト（手動テスト項目）
- [ ] 収支登録画面でのデータ入力・送信
- [ ] 一覧画面でのデータ表示・編集・削除
- [ ] レポート画面での集計表示
- [ ] 各画面間の遷移動作
- [ ] モバイル端末での操作性

---

## 🛡️ セキュリティ・品質

### セキュリティ対策

#### 1. 入力検証
```java
// コントローラーレベル
@PostMapping
public ResponseEntity<Infokanri> add(
    @Valid @RequestBody Infokanri infokanri) {
    // バリデーション処理
}

// エンティティレベル
@Entity
public class Infokanri {
    @NotNull
    @Size(max = 50)
    private String syubetu;
    
    @NotNull
    @Min(0)
    private Integer kingaku;
}
```

#### 2. SQLインジェクション対策
- Spring Data JPA の自動生成クエリ使用
- パラメータ化クエリの徹底
- 入力値の事前検証

#### 3. XSS対策
```javascript
// JavaScript でのエスケープ処理
function escapeHtml(text) {
  const div = document.createElement('div');
  div.textContent = text;
  return div.innerHTML;
}
```

### 品質管理

#### 1. コード品質
```xml
<!-- pom.xml - Spotless プラグイン -->
<plugin>
    <groupId>com.diffplug.spotless</groupId>
    <artifactId>spotless-maven-plugin</artifactId>
    <version>2.43.0</version>
    <configuration>
        <java>
            <googleJavaFormat/>
        </java>
    </configuration>
</plugin>
```

#### 2. 静的解析
```bash
# コードフォーマット
mvn spotless:apply

# 品質チェック
mvn spotless:check
```

---

## 🚨 トラブルシューティング

### よくある問題と対処法

#### 1. データベース接続エラー
```bash
# 症状
org.springframework.jdbc.CannotGetJdbcConnectionException

# 対処法
1. 環境変数 DATABASE_URL の確認
2. PostgreSQL サービスの稼働状況確認
3. 接続文字列の形式確認（sslmode=require）
```

#### 2. JSON レスポンス空文字問題
```java
// 原因: Jackson シリアライゼーション設定不備
// 対処法: application-production.properties に設定追加
spring.jackson.serialization.write-dates-as-timestamps=false
spring.jackson.time-zone=Asia/Tokyo
```

#### 3. CORS エラー
```java
// 原因: クロスオリジンリクエストブロック
// 対処法: CorsConfig.java で適切な設定
@Configuration
public class CorsConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                    .allowedOrigins("*")
                    .allowedMethods("*");
            }
        };
    }
}
```

#### 4. メモリ不足エラー
```bash
# 症状
OutOfMemoryError

# 対処法
JAVA_TOOL_OPTIONS=-XX:MaxRAMPercentage=50.0
```

---

## 📈 改善提案・将来計画

### 短期改善項目（1-3ヶ月）

#### 1. 機能追加
- [ ] データエクスポート機能（CSV/Excel）
- [ ] 検索・フィルタリング機能
- [ ] カテゴリのカスタマイズ機能
- [ ] データバックアップ・復元機能

#### 2. UI/UX改善
- [ ] ダークモード対応
- [ ] キーボードショートカット
- [ ] 操作履歴（Undo/Redo）
- [ ] PWA対応

#### 3. 技術的改善
- [ ] フロントエンドフレームワーク導入（React/Vue.js）
- [ ] TypeScript導入
- [ ] テストカバレッジ向上（80%以上）
- [ ] CI/CD パイプライン強化

### 中期改善項目（3-6ヶ月）

#### 1. 機能拡張
- [ ] 予算管理機能
- [ ] 定期支出・収入管理
- [ ] 通知機能
- [ ] 多通貨対応

#### 2. セキュリティ強化
- [ ] ユーザー認証システム
- [ ] OAuth2.0 連携
- [ ] データ暗号化
- [ ] 監査ログ機能

#### 3. パフォーマンス最適化
- [ ] CDNの活用
- [ ] キャッシュ戦略
- [ ] 画像最適化
- [ ] 遅延読み込み

### 長期改善項目（6ヶ月以上）

#### 1. プラットフォーム拡張
- [ ] モバイルアプリ化（React Native/Flutter）
- [ ] デスクトップアプリ化（Electron）
- [ ] 他社連携（銀行API、クレジットカードAPI）

#### 2. 高度な機能
- [ ] 機械学習による支出予測
- [ ] 画像認識によるレシート自動入力
- [ ] 音声入力対応
- [ ] AI アシスタント機能

---

## 📚 関連ドキュメント

### 開発ドキュメント
- [README.md](./README.md) - プロジェクト概要
- [APPLICATION_SPEC.md](./APPLICATION_SPEC.md) - 旧仕様書
- [render-postgresql-setup.md](./render-postgresql-setup.md) - DB設定手順
- [render-environment-setup.md](./render-environment-setup.md) - 環境設定手順

### 外部リンク
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Render Platform Documentation](https://render.com/docs)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [Font Awesome](https://fontawesome.com/)

---

## 🏷️ 免責事項・ライセンス

### 注意事項
- 本アプリケーションは学習・個人利用目的で作成されています
- 商用利用時は適切なライセンス確認を行ってください
- データの安全性について十分に検討してください
- 本番環境でのセキュリティ対策を必ず実施してください

### ライセンス
- アプリケーションコード: オープンソース（詳細は要確認）
- 使用ライブラリ: 各ライブラリのライセンスに従う
- Font Awesome: SIL OFL 1.1 License

---

**この仕様書は現在のアプリケーション状態を正確に反映しており、新規プロジェクト立ち上げ時の完全な参考資料として活用できます。**

**作成日: 2025年6月22日**  
**最終更新: 2025年6月22日**  
**バージョン: 1.0.0**
