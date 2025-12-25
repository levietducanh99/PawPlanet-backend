/* =========================
   1. CREATE SCHEMAS
========================= */

CREATE SCHEMA IF NOT EXISTS auth;
CREATE SCHEMA IF NOT EXISTS social;
CREATE SCHEMA IF NOT EXISTS pet;
CREATE SCHEMA IF NOT EXISTS encyclopedia;
CREATE SCHEMA IF NOT EXISTS notification;

/* =========================
   2. AUTH SCHEMA
========================= */

CREATE TABLE auth.users (
                            id bigserial PRIMARY KEY,
                            email varchar UNIQUE NOT NULL,
                            username varchar UNIQUE NOT NULL,
                            password varchar NOT NULL,
                            role varchar,
                            avatar_url text,
                            bio text,
                            is_verified boolean,
                            created_at timestamp,
                            deleted_at timestamp
);

CREATE TABLE auth.follow_user (
                                  follower_id bigint,
                                  following_id bigint,
                                  created_at timestamp,
                                  PRIMARY KEY (follower_id, following_id)
);

/* =========================
   3. ENCYCLOPEDIA SCHEMA
========================= */

CREATE TABLE encyclopedia.species (
                                      id bigserial PRIMARY KEY,
                                      name varchar,
                                      display_name varchar
);

CREATE TABLE encyclopedia.breeds (
                                     id bigserial PRIMARY KEY,
                                     species_id bigint,
                                     name varchar,
                                     short_description text
);

CREATE TABLE encyclopedia.encyclopedia_sections (
                                                    id bigserial PRIMARY KEY,
                                                    code varchar UNIQUE,
                                                    display_name varchar,
                                                    description text
);

CREATE TABLE encyclopedia.breed_sections (
                                             breed_id bigint,
                                             section_id bigint,
                                             display_order int,
                                             PRIMARY KEY (breed_id, section_id)
);

CREATE TABLE encyclopedia.breed_section_contents (
                                                     id bigserial PRIMARY KEY,
                                                     breed_id bigint,
                                                     section_id bigint,
                                                     language varchar,
                                                     content text
);

/* =========================
   4. PET SCHEMA
========================= */

CREATE TABLE pet.pets (
                          id bigserial PRIMARY KEY,
                          name varchar,
                          species varchar,
                          breed_id bigint,
                          age int,
                          gender varchar,
                          health_status text,
                          owner_id bigint
);

CREATE TABLE pet.pet_media (
                               id bigserial PRIMARY KEY,
                               pet_id bigint,
                               type varchar,
                               url text
);

CREATE TABLE pet.follow_pet (
                                user_id bigint,
                                pet_id bigint,
                                created_at timestamp,
                                PRIMARY KEY (user_id, pet_id)
);

/* =========================
   5. SOCIAL SCHEMA
========================= */

CREATE TABLE social.posts (
                              id bigserial PRIMARY KEY,
                              author_id bigint,
                              content text,
                              hashtags text,
                              type varchar,
                              contact_info varchar,
                              location varchar,
                              created_at timestamp,
                              deleted_at timestamp
);

CREATE TABLE social.post_media (
                                   id bigserial PRIMARY KEY,
                                   post_id bigint,
                                   type varchar,
                                   url text
);

CREATE TABLE social.post_pet (
                                 post_id bigint,
                                 pet_id bigint,
                                 PRIMARY KEY (post_id, pet_id)
);

CREATE TABLE social.comments (
                                 id bigserial PRIMARY KEY,
                                 post_id bigint,
                                 user_id bigint,
                                 parent_id bigint,
                                 content text,
                                 created_at timestamp
);

CREATE TABLE social.likes (
                              user_id bigint,
                              post_id bigint,
                              created_at timestamp,
                              PRIMARY KEY (user_id, post_id)
);

/* =========================
   6. NOTIFICATION SCHEMA
========================= */

CREATE TABLE notification.notifications (
                                            id bigserial PRIMARY KEY,
                                            user_id bigint,
                                            type varchar,
                                            reference_id bigint,
                                            is_read boolean,
                                            created_at timestamp
);

/* =========================
   7. FOREIGN KEYS
========================= */

-- AUTH
ALTER TABLE auth.follow_user
    ADD CONSTRAINT fk_follow_user_follower
        FOREIGN KEY (follower_id) REFERENCES auth.users(id);

ALTER TABLE auth.follow_user
    ADD CONSTRAINT fk_follow_user_following
        FOREIGN KEY (following_id) REFERENCES auth.users(id);

-- ENCYCLOPEDIA
ALTER TABLE encyclopedia.breeds
    ADD CONSTRAINT fk_breed_species
        FOREIGN KEY (species_id) REFERENCES encyclopedia.species(id);

ALTER TABLE encyclopedia.breed_sections
    ADD CONSTRAINT fk_breed_sections_breed
        FOREIGN KEY (breed_id) REFERENCES encyclopedia.breeds(id);

ALTER TABLE encyclopedia.breed_sections
    ADD CONSTRAINT fk_breed_sections_section
        FOREIGN KEY (section_id) REFERENCES encyclopedia.encyclopedia_sections(id);

ALTER TABLE encyclopedia.breed_section_contents
    ADD CONSTRAINT fk_breed_section_contents_breed
        FOREIGN KEY (breed_id) REFERENCES encyclopedia.breeds(id);

ALTER TABLE encyclopedia.breed_section_contents
    ADD CONSTRAINT fk_breed_section_contents_section
        FOREIGN KEY (section_id) REFERENCES encyclopedia.encyclopedia_sections(id);

-- PET
ALTER TABLE pet.pets
    ADD CONSTRAINT fk_pet_owner
        FOREIGN KEY (owner_id) REFERENCES auth.users(id);

ALTER TABLE pet.pets
    ADD CONSTRAINT fk_pet_breed
        FOREIGN KEY (breed_id) REFERENCES encyclopedia.breeds(id);

ALTER TABLE pet.pet_media
    ADD CONSTRAINT fk_pet_media_pet
        FOREIGN KEY (pet_id) REFERENCES pet.pets(id);

ALTER TABLE pet.follow_pet
    ADD CONSTRAINT fk_follow_pet_user
        FOREIGN KEY (user_id) REFERENCES auth.users(id);

ALTER TABLE pet.follow_pet
    ADD CONSTRAINT fk_follow_pet_pet
        FOREIGN KEY (pet_id) REFERENCES pet.pets(id);

-- SOCIAL
ALTER TABLE social.posts
    ADD CONSTRAINT fk_post_author
        FOREIGN KEY (author_id) REFERENCES auth.users(id);

ALTER TABLE social.post_media
    ADD CONSTRAINT fk_post_media_post
        FOREIGN KEY (post_id) REFERENCES social.posts(id);

ALTER TABLE social.post_pet
    ADD CONSTRAINT fk_post_pet_post
        FOREIGN KEY (post_id) REFERENCES social.posts(id);

ALTER TABLE social.post_pet
    ADD CONSTRAINT fk_post_pet_pet
        FOREIGN KEY (pet_id) REFERENCES pet.pets(id);

ALTER TABLE social.comments
    ADD CONSTRAINT fk_comment_post
        FOREIGN KEY (post_id) REFERENCES social.posts(id);

ALTER TABLE social.comments
    ADD CONSTRAINT fk_comment_user
        FOREIGN KEY (user_id) REFERENCES auth.users(id);

ALTER TABLE social.comments
    ADD CONSTRAINT fk_comment_parent
        FOREIGN KEY (parent_id) REFERENCES social.comments(id);

ALTER TABLE social.likes
    ADD CONSTRAINT fk_like_user
        FOREIGN KEY (user_id) REFERENCES auth.users(id);

ALTER TABLE social.likes
    ADD CONSTRAINT fk_like_post
        FOREIGN KEY (post_id) REFERENCES social.posts(id);

-- NOTIFICATION
ALTER TABLE notification.notifications
    ADD CONSTRAINT fk_notification_user
        FOREIGN KEY (user_id) REFERENCES auth.users(id);
