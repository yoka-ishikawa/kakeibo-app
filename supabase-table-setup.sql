-- ===================================================================
-- 家計簿アプリケーション用テーブル作成 (Supabase)
-- ===================================================================

-- 既存テーブルがある場合は削除（必要に応じて）
-- DROP TABLE IF EXISTS tb_info_kanri;

-- 新しいテーブル作成
CREATE TABLE IF NOT EXISTS tb_info_kanri (
    id SERIAL PRIMARY KEY,
    user_id VARCHAR(255),
    syubetu VARCHAR(50) NOT NULL,
    kingaku INTEGER NOT NULL,
    naisyo VARCHAR(255),
    hiduke DATE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- インデックス作成
CREATE INDEX IF NOT EXISTS idx_tb_info_kanri_user_id ON tb_info_kanri(user_id);
CREATE INDEX IF NOT EXISTS idx_tb_info_kanri_hiduke ON tb_info_kanri(hiduke);
CREATE INDEX IF NOT EXISTS idx_tb_info_kanri_syubetu ON tb_info_kanri(syubetu);

-- サンプルデータ挿入（テスト用）
INSERT INTO tb_info_kanri (user_id, syubetu, kingaku, naisyo, hiduke) VALUES
('test-user-1', '収入', 300000, '給与', '2025-06-01'),
('test-user-1', '支出', 80000, '家賃', '2025-06-01'),
('test-user-1', '支出', 15000, '食費', '2025-06-15'),
('test-user-1', '収入', 5000, 'アルバイト', '2025-06-20');

-- テーブル確認
SELECT 
    table_name, 
    column_name, 
    data_type, 
    is_nullable, 
    column_default
FROM information_schema.columns 
WHERE table_name = 'tb_info_kanri' 
ORDER BY ordinal_position;

-- データ確認
SELECT * FROM tb_info_kanri ORDER BY hiduke DESC, id DESC;
