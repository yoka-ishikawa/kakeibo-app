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

### 設計資
- [要件定義](https://docs.google.com/spreadsheets/d/1Zykgngd74m_7o4Q2fOFoc-ADYFo8yUvBVfWy_AblrVE/edit?usp=sharing)
- [基本設計 - メニュー画面](https://1drv.ms/x/c/58cea19fddb0da42/EctnvARHl_1FmGneeVdNnLIBMI-n8h4nmD3XnTzzbs-xnA?e=PEHUKN)
- [基本設計 - 収支登録画面](https://1drv.ms/x/c/58cea19fddb0da42/EVyvpGkGaGVGp8XeeQBbI7kBVDU4-XgAOXP1F1zyWZVlBA?e=N2oalV)
- [基本設計 - 一覧画面](https://1drv.ms/x/c/58cea19fddb0da42/EaIhIxqI35xFuMFjpUAkNCMBfga0_g3ZKbE1hzxDb1g_vA?e=QgpCA5)
- [基本設計 - レポート画面](https://1drv.ms/x/c/58cea19fddb0da42/EUL2JbgW-OdMl7ATgPu4gj4B6MTztEyBz1lGiEzeMQGz9A?e=gy3KWi)
- [詳細設計 - メニュー画面](https://1drv.ms/x/c/58cea19fddb0da42/EcKZ7gmdm5xOj8zTy69TcZAB2YB6SliVgg7Cg0-Hypz48w?e=0rwCcG)
- [詳細設計 - 収支登録画面](https://1drv.ms/x/c/58cea19fddb0da42/EVyvpGkGaGVGp8XeeQBbI7kBVDU4-XgAOXP1F1zyWZVlBA?e=ZZoGzb)
- [詳細設計 - 一覧画面](https://1drv.ms/x/c/58cea19fddb0da42/Ec7gR07SUoVIo22njOPJkSkBmByaiNvFMmQCfgG1JHZ4Mg?e=vKElcw)
- [詳細設計 - レポート画面](https://1drv.ms/x/c/58cea19fddb0da42/EVFSIMDxAWFDjeFK1Qhwk_gBL043GGE044uD39imDyT1jg?e=EC0YyT)
- [詳細設計 - 収支登録API【POST】](https://1drv.ms/x/c/58cea19fddb0da42/EVZtKcxf7LNGiYKgMnysDEYBscoCRylKztNjr-3Wcy_9nQ?e=Zrzp6B)
- [詳細設計 - 収支取得API【GET】](https://1drv.ms/x/c/58cea19fddb0da42/EW-xQiqKZCtCgM97YL11nBUBvpbAw9Btsw_7jQ0IQHVwPw?e=okYptE)
- [詳細設計 - 収支変更API【PUT】](https://1drv.ms/x/c/58cea19fddb0da42/EVZtKcxf7LNGiYKgMnysDEYBscoCRylKztNjr-3Wcy_9nQ?e=Zrzp6B)
- [詳細設計 - 収支削除API【DELETE】](https://1drv.ms/x/c/58cea19fddb0da42/EevqjSATXG5HtnVdPQkDBTcBgqyib_MEoz60-JCNnUCk0g?e=jgXc2r)
- [WBS](https://docs.google.com/spreadsheets/d/1NNerjxjCcL9oa6iLdykSz05dcb6O8V50o1Q_8gNreIo/edit?gid=0#gid=0)
