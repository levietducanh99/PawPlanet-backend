-- V4: Loại bỏ breed_section_mapping và chuyển display_order sang breed_section_contents
-- Lý do: breed_section_contents đã vừa là mapping vừa là content, không cần bảng mapping riêng

-- Bước 1: Thêm cột display_order vào breed_section_contents (nếu chưa có)
ALTER TABLE encyclopedia.breed_section_contents
ADD COLUMN IF NOT EXISTS display_order INTEGER;

-- Bước 2: Copy display_order từ breed_section_mapping sang breed_section_contents
-- Chỉ update nếu đã có mapping data
UPDATE encyclopedia.breed_section_contents bsc
SET display_order = bsm.display_order
FROM encyclopedia.breed_section_mapping bsm
WHERE bsc.breed_id = bsm.breed_id
  AND bsc.section_id = bsm.section_id
  AND bsc.display_order IS NULL;

-- Bước 3: Set display_order mặc định cho records chưa có (theo section_id)
UPDATE encyclopedia.breed_section_contents
SET display_order = section_id::INTEGER
WHERE display_order IS NULL;

-- Bước 4: Drop bảng breed_section_mapping
DROP TABLE IF EXISTS encyclopedia.breed_section_mapping;

-- Bước 5: Thêm comment giải thích
COMMENT ON COLUMN encyclopedia.breed_section_contents.display_order IS 'Thứ tự hiển thị section trong breed detail page';

COMMENT ON TABLE encyclopedia.breed_section_contents IS 'Nội dung chi tiết cho section của Breed. Nếu có record (breed_id, section_id) thì breed đó có section đó (vừa là mapping vừa là content).';

