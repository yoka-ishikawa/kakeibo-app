-- 家計簿情報管理テーブル
CREATE TABLE IF NOT EXISTS tb_info_kanri (
    id BIGSERIAL PRIMARY KEY,
    user_token VARCHAR(255),
    registered_at DATE,
    type VARCHAR(255),
    category VARCHAR(255),
    amount INTEGER,
    update_date_time TIMESTAMP
);