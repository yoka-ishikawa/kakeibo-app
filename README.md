# 家計簿管理アプリ (Kakeibo App）

### ローカル開発環境
**URL**:http://localhost:8080
**DB**:H2 Database

## 本番環境
**URL**: [https://kakeibo-app-gy0m.onrender.com](https://kakeibo-app-gy0m.onrender.com) 
**DB**:PostgreSQL (Render PostgreSQL)

## 機能一覧

### 主要機能
- **収支登録**: 収入・支出の登録
- **データ一覧**: 登録したデータの一覧表示・編集・削除
- **レポート**: 月間・年間の収支集計

### 画面構成
1. **メニュー画面** (`index.html`) - メニュー
2. **収支登録画面** (`inexpen.html`) - データ入力フォーム
3. **収支一覧画面** (`list.html`) - データ管理
4. **レポート画面** (`report.html`) - 集計

### 設計資料  
- [要件定義](https://docs.google.com/spreadsheets/d/1Zykgngd74m_7o4Q2fOFoc-ADYFo8yUvBVfWy_AblrVE/edit?usp=sharing)
- [基本設計 - メニュー画面](https://1drv.ms/x/c/58cea19fddb0da42/EctnvARHl_1FmGneeVdNnLIBMI-n8h4nmD3XnTzzbs-xnA?e=PEHUKN)
- [基本設計 - 収支登録画面](https://1drv.ms/x/c/58cea19fddb0da42/EVyvpGkGaGVGp8XeeQBbI7kBVDU4-XgAOXP1F1zyWZVlBA?e=N2oalV)
- [基本設計 - 一覧画面](https://1drv.ms/x/c/58cea19fddb0da42/EaIhIxqI35xFuMFjpUAkNCMBfga0_g3ZKbE1hzxDb1g_vA?e=QgpCA5)
- [基本設計 - レポート画面](https://1drv.ms/x/c/58cea19fddb0da42/EUL2JbgW-OdMl7ATgPu4gj4B6MTztEyBz1lGiEzeMQGz9A?e=gy3KWi)
- [詳細設計 - メニュー画面](https://onedrive.live.com/personal/58cea19fddb0da42/_layouts/15/doc2.aspx?resid=09ee99c2-9b9d-4e9c-8fcc-d3cbaf537190&cid=58cea19fddb0da42&ct=1750662950522&wdOrigin=OFFICECOM-WEB.MAIN.EDGEWORTH&wdPreviousSessionSrc=HarmonyWeb&wdPreviousSession=00433678-69bc-4db4-963b-0d3d74fcbcb8)
- [詳細設計 - 収支登録画面](https://onedrive.live.com/personal/58cea19fddb0da42/_layouts/15/doc.aspx?resid=30e53f87-7eb3-47f5-bacf-6ee8d5514aef&cid=58cea19fddb0da42&ct=1750663375958&wdOrigin=OFFICECOM-WEB.MAIN.EDGEWORTH&wdPreviousSessionSrc=HarmonyWeb&wdPreviousSession=00433678-69bc-4db4-963b-0d3d74fcbcb8)
- [詳細設計 - 一覧画面](https://onedrive.live.com/personal/58cea19fddb0da42/_layouts/15/doc.aspx?resid=4e47e0ce-52d2-4885-a36d-a78ce3c99129&cid=58cea19fddb0da42&ct=1750664133178&wdOrigin=OFFICECOM-WEB.MAIN.EDGEWORTH&wdPreviousSessionSrc=HarmonyWeb&wdPreviousSession=00433678-69bc-4db4-963b-0d3d74fcbcb8)
- [詳細設計 - レポート画面](https://onedrive.live.com/personal/58cea19fddb0da42/_layouts/15/doc.aspx?resid=c0205251-01f1-4361-8de1-4ad5087093f8&cid=58cea19fddb0da42&ct=1750664258885&wdOrigin=OFFICECOM-WEB.MAIN.EDGEWORTH&wdPreviousSessionSrc=HarmonyWeb&wdPreviousSession=00433678-69bc-4db4-963b-0d3d74fcbcb8)
- [詳細設計 - 収支登録API【POST】]()
- [詳細設計 - 収支取得API【GET】]()
- [詳細設計 - 収支変更API【PUT】]()
- [詳細設計 - 収支削除API【DELETE】]()
- [WBS](https://docs.google.com/spreadsheets/d/1NNerjxjCcL9oa6iLdykSz05dcb6O8V50o1Q_8gNreIo/edit?gid=0#gid=0)
