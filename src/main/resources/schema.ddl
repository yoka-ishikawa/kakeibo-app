-- ===================================================================
-- 家計簿アプリケーション用テーブル (PostgreSQL)
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