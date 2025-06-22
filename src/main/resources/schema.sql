-- ===================================================================
-- 家計簿アプリケーション用テーブル (PostgreSQL/Supabase対応)
-- ===================================================================

-- 家計簿情報管理テーブル
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

-- インデックス作成（パフォーマンス向上のため）
CREATE INDEX IF NOT EXISTS idx_tb_info_kanri_user_id ON tb_info_kanri(user_id);
CREATE INDEX IF NOT EXISTS idx_tb_info_kanri_hiduke ON tb_info_kanri(hiduke);
CREATE INDEX IF NOT EXISTS idx_tb_info_kanri_syubetu ON tb_info_kanri(syubetu);