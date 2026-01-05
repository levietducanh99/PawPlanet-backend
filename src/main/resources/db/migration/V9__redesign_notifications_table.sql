-- Redesign notifications table to be flexible and extensible
-- New design principles:
-- 1. No separate tables for each notification type
-- 2. No DB migration needed when adding new notification types
-- 3. FE renders based on type + metadata
-- 4. BE emits event → creates notification

-- Drop old indexes if they exist
DROP INDEX IF EXISTS notification.idx_notifications_user_id;

-- Drop existing foreign key constraint before renaming column
ALTER TABLE notification.notifications
    DROP CONSTRAINT IF EXISTS notifications_user_id_fkey;

-- Rename/modify existing columns and add new ones
ALTER TABLE notification.notifications
    -- Rename user_id to recipient_id for clarity
    RENAME COLUMN user_id TO recipient_id;

-- Drop old reference_id column (replaced by target_id + target_type)
ALTER TABLE notification.notifications
    DROP COLUMN IF EXISTS reference_id;

-- Add new columns
ALTER TABLE notification.notifications
    ADD COLUMN IF NOT EXISTS actor_id BIGINT,
    ADD COLUMN IF NOT EXISTS target_type VARCHAR(50),
    ADD COLUMN IF NOT EXISTS target_id BIGINT,
    ADD COLUMN IF NOT EXISTS metadata JSONB;

-- Update type column to VARCHAR(50) if needed
ALTER TABLE notification.notifications
    ALTER COLUMN type TYPE VARCHAR(50);

-- Add foreign key constraints
ALTER TABLE notification.notifications
    ADD CONSTRAINT fk_notifications_recipient
    FOREIGN KEY (recipient_id) REFERENCES auth.users(id) ON DELETE CASCADE;

ALTER TABLE notification.notifications
    ADD CONSTRAINT fk_notifications_actor
    FOREIGN KEY (actor_id) REFERENCES auth.users(id) ON DELETE SET NULL;

-- Create indexes for performance
CREATE INDEX IF NOT EXISTS idx_notifications_recipient
    ON notification.notifications(recipient_id);

CREATE INDEX IF NOT EXISTS idx_notifications_unread
    ON notification.notifications(recipient_id, is_read);

CREATE INDEX IF NOT EXISTS idx_notifications_type
    ON notification.notifications(type);

CREATE INDEX IF NOT EXISTS idx_notifications_actor
    ON notification.notifications(actor_id);

CREATE INDEX IF NOT EXISTS idx_notifications_created_at
    ON notification.notifications(created_at DESC);

-- Add comments for documentation
COMMENT ON COLUMN notification.notifications.recipient_id IS 'Người nhận notification';
COMMENT ON COLUMN notification.notifications.actor_id IS 'Người thực hiện hành động (nullable cho system notifications)';
COMMENT ON COLUMN notification.notifications.type IS 'Loại notification: FOLLOW_USER, FOLLOW_PET, LIKE_POST, COMMENT_POST, SYSTEM, etc.';
COMMENT ON COLUMN notification.notifications.target_type IS 'Loại đối tượng: USER, PET, POST, COMMENT, etc.';
COMMENT ON COLUMN notification.notifications.target_id IS 'ID của target (tùy theo target_type)';
COMMENT ON COLUMN notification.notifications.metadata IS 'Dữ liệu mở rộng dạng JSON cho FE (actorUsername, petName, postPreview, etc.)';
COMMENT ON COLUMN notification.notifications.is_read IS 'Trạng thái đã đọc';
COMMENT ON COLUMN notification.notifications.created_at IS 'Thời điểm tạo notification';

COMMENT ON TABLE notification.notifications IS 'Bảng notifications linh hoạt - Notification = (actor) làm gì (type) lên (target) → gửi cho (recipient)';

