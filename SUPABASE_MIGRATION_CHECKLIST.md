# Supabase移行実行手順書

## 📋 **実行チェックリスト**

### **Phase 2: Supabaseプロジェクト作成**

#### ✅ **Step 1: アカウント作成**
- [ ] https://supabase.com にアクセス
- [ ] 「Start your project」をクリック  
- [ ] GitHubアカウントでサインアップ完了

#### ✅ **Step 2: 新プロジェクト作成**
- [ ] ダッシュボードで「New Project」クリック
- [ ] プロジェクト設定入力:
  - **Organization**: Personal（個人アカウント）
  - **Name**: `kakeibo-app`
  - **Database Password**: `[強力なパスワード設定]`
  - **Region**: `Northeast Asia (Tokyo)`
  - **Pricing Plan**: Free tier
- [ ] 「Create new project」クリック
- [ ] プロジェクト作成完了（数分待機）

---

### **Phase 3: データベースセットアップ**

#### ✅ **Step 3: スキーマ作成**
1. Supabaseダッシュボード → 「SQL Editor」
2. `supabase-schema.sql` の内容をコピー&ペースト
3. 「Run」をクリックして実行
4. 実行結果でテーブル構造確認

#### ✅ **Step 4: データインポート** 
1. 同じSQL Editorで新しいクエリ作成
2. `supabase-data-import.sql` の内容をコピー&ペースト
3. 「Run」をクリックして実行
4. 実行結果で21件のデータインポート確認

---

### **Phase 4: 接続情報取得**

#### ✅ **Step 5: 接続文字列取得**
1. Supabaseダッシュボード → 「Settings」 → 「Database」
2. 「Connection string」セクションで「URI」選択
3. 接続文字列をコピー: 
   ```
   postgresql://postgres:[YOUR-PASSWORD]@db.[PROJECT-REF].supabase.co:5432/postgres
   ```
4. 以下の情報をメモ:
   - **Host**: `db.[PROJECT-REF].supabase.co`
   - **Port**: `5432` 
   - **Database**: `postgres`
   - **Username**: `postgres`
   - **Password**: `[設定したパスワード]`

---

### **Phase 5: アプリケーション設定変更**

#### ✅ **Step 6: Render環境変数設定**
Renderダッシュボード → kakeibo-app → Environment で以下を設定:

```bash
SPRING_PROFILES_ACTIVE=production-supabase
DATABASE_URL=postgresql://postgres:[YOUR-PASSWORD]@db.[PROJECT-REF].supabase.co:5432/postgres  
DB_USERNAME=postgres
DB_PASSWORD=[YOUR-PASSWORD]
```

**⚠️ 重要**: `[YOUR-PASSWORD]`と`[PROJECT-REF]`を実際の値に置換

---

### **Phase 6: デプロイとテスト**

#### ✅ **Step 7: アプリケーションデプロイ**
1. GitHubに変更をコミット&プッシュ
2. Renderで自動デプロイ実行確認
3. デプロイログで「production-supabase」プロファイル使用確認
4. エラーがないことを確認

#### ✅ **Step 8: 動作確認**  
1. https://kakeibo-app-gy0m.onrender.com にアクセス
2. データ表示確認（21件のデータが表示されるか）
3. 新規データ登録テスト
4. データ削除・更新テスト
5. レポート機能動作確認

---

### **Phase 7: 切り替え完了後**

#### ✅ **Step 9: 旧データベース削除**
- [ ] 新環境での動作確認完了後
- [ ] Renderの旧PostgreSQLインスタンス削除
- [ ] 旧環境変数のクリーンアップ

#### ✅ **Step 10: ドキュメント更新**
- [ ] README.mdのデータベース情報更新  
- [ ] 設定ファイルの説明更新

---

## 🚨 **緊急時のロールバック手順**

問題発生時の復旧手順:
1. Render環境変数を元の設定に戻す
2. `SPRING_PROFILES_ACTIVE=production` に変更
3. Renderで再デプロイ実行
4. 旧データベースに復帰

---

## 📞 **サポート情報**

- **Render Dashboard**: https://dashboard.render.com
- **Supabase Dashboard**: https://app.supabase.com  
- **GitHub Repository**: https://github.com/yoka-ishikawa/kakeibo-app

---

**作成日**: 2025年7月19日  
**有効期限**: 2025年7月22日（Render DB期限前）  
**実行者**: [ 実行者名を記入 ]  
**実行日時**: [ 実行日時を記入 ]
