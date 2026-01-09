-- Add soft delete fields to encyclopedia.media
ALTER TABLE "encyclopedia"."media"
    ADD COLUMN "is_deleted" BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN "deleted_at" TIMESTAMP NULL,
    ADD COLUMN "deleted_by" BIGINT NULL;

-- Add soft delete fields to social.posts (is_deleted and deleted_by, deleted_at already exists)
ALTER TABLE "social"."posts"
    ADD COLUMN "is_deleted" BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN "deleted_by" BIGINT NULL;

-- Add soft delete fields to pet.pets
ALTER TABLE "pet"."pets"
    ADD COLUMN "is_deleted" BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN "deleted_at" TIMESTAMP NULL,
    ADD COLUMN "deleted_by" BIGINT NULL;

-- Add soft delete fields to social.comments (is_deleted and deleted_by, deleted_at already exists)
ALTER TABLE "social"."comments"
    ADD COLUMN "is_deleted" BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN "deleted_by" BIGINT NULL;

-- Add soft delete fields to social.post_media
ALTER TABLE "social"."post_media"
    ADD COLUMN "is_deleted" BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN "deleted_at" TIMESTAMP NULL,
    ADD COLUMN "deleted_by" BIGINT NULL;

-- Add soft delete fields to pet.pet_media
ALTER TABLE "pet"."pet_media"
    ADD COLUMN "is_deleted" BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN "deleted_at" TIMESTAMP NULL,
    ADD COLUMN "deleted_by" BIGINT NULL;
