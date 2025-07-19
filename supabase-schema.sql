-- ===================================================================
-- 家計簿アプリケーション - Supabaseデータベーススキーマ作成
-- 実行順序: 1. スキーマ作成 → 2. データインポート
-- ===================================================================

-- シーケンス作成
CREATE SEQUENCE IF NOT EXISTS tb_info_kanri_id_seq;

-- テーブル作成
CREATE TABLE IF NOT EXISTS tb_info_kanri (
    id INTEGER NOT NULL DEFAULT nextval('tb_info_kanri_id_seq'::regclass),
    user_id VARCHAR,
    type VARCHAR NOT NULL,
    amount INTEGER NOT NULL,
    category VARCHAR,
    registed_at DATE NOT NULL,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);

-- シーケンスの所有者設定
ALTER SEQUENCE tb_info_kanri_id_seq OWNED BY tb_info_kanri.id;

-- パフォーマンス向上のためのインデックス作成
CREATE INDEX IF NOT EXISTS idx_tb_info_kanri_registed_at ON tb_info_kanri(registed_at);
CREATE INDEX IF NOT EXISTS idx_tb_info_kanri_type ON tb_info_kanri(type);
CREATE INDEX IF NOT EXISTS idx_tb_info_kanri_user_id ON tb_info_kanri(user_id);
CREATE INDEX IF NOT EXISTS idx_tb_info_kanri_created_at ON tb_info_kanri(created_at);

-- テーブル作成確認
SELECT 
    table_name, 
    column_name, 
    data_type, 
    is_nullable,
    column_default
FROM information_schema.columns 
WHERE table_name = 'tb_info_kanri' 
ORDER BY ordinal_position;
