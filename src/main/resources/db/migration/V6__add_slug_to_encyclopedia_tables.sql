-- Migration V6: Add slug column to encyclopedia tables
-- Purpose: Add slug field for URL-friendly identifiers

-- Add slug column to classes table
ALTER TABLE encyclopedia.classes
ADD COLUMN IF NOT EXISTS slug VARCHAR(255);

-- Add slug column to species table
ALTER TABLE encyclopedia.species
ADD COLUMN IF NOT EXISTS slug VARCHAR(255);

-- Add slug column to breeds table
ALTER TABLE encyclopedia.breeds
ADD COLUMN IF NOT EXISTS slug VARCHAR(255);

-- Generate slugs for existing data in classes
UPDATE encyclopedia.classes
SET slug = LOWER(
    REGEXP_REPLACE(
        REGEXP_REPLACE(
            REGEXP_REPLACE(name, '[àáạảãâầấậẩẫăằắặẳẵ]', 'a', 'gi'),
            '[èéẹẻẽêềếệểễ]', 'e', 'gi'
        ),
        '[^a-z0-9]+', '-', 'gi'
    )
)
WHERE slug IS NULL;

-- Generate slugs for existing data in species
UPDATE encyclopedia.species
SET slug = LOWER(
    REGEXP_REPLACE(
        REGEXP_REPLACE(
            REGEXP_REPLACE(name, '[àáạảãâầấậẩẫăằắặẳẵ]', 'a', 'gi'),
            '[èéẹẻẽêềếệểễ]', 'e', 'gi'
        ),
        '[^a-z0-9]+', '-', 'gi'
    )
)
WHERE slug IS NULL;

-- Generate slugs for existing data in breeds
UPDATE encyclopedia.breeds
SET slug = LOWER(
    REGEXP_REPLACE(
        REGEXP_REPLACE(
            REGEXP_REPLACE(name, '[àáạảãâầấậẩẫăằắặẳẵ]', 'a', 'gi'),
            '[èéẹẻẽêềếệểễ]', 'e', 'gi'
        ),
        '[^a-z0-9]+', '-', 'gi'
    )
)
WHERE slug IS NULL;

-- Clean up slugs: remove leading/trailing hyphens
UPDATE encyclopedia.classes
SET slug = TRIM(BOTH '-' FROM slug);

UPDATE encyclopedia.species
SET slug = TRIM(BOTH '-' FROM slug);

UPDATE encyclopedia.breeds
SET slug = TRIM(BOTH '-' FROM slug);

-- Handle duplicate slugs by appending ID
-- Classes
UPDATE encyclopedia.classes c1
SET slug = slug || '-' || id
WHERE EXISTS (
    SELECT 1 FROM encyclopedia.classes c2
    WHERE c2.slug = c1.slug AND c2.id < c1.id
);

-- Species
UPDATE encyclopedia.species s1
SET slug = slug || '-' || id
WHERE EXISTS (
    SELECT 1 FROM encyclopedia.species s2
    WHERE s2.slug = s1.slug AND s2.id < s1.id
);

-- Breeds
UPDATE encyclopedia.breeds b1
SET slug = slug || '-' || id
WHERE EXISTS (
    SELECT 1 FROM encyclopedia.breeds b2
    WHERE b2.slug = b1.slug AND b2.id < b1.id
);

-- Make slug NOT NULL
ALTER TABLE encyclopedia.classes
ALTER COLUMN slug SET NOT NULL;

ALTER TABLE encyclopedia.species
ALTER COLUMN slug SET NOT NULL;

ALTER TABLE encyclopedia.breeds
ALTER COLUMN slug SET NOT NULL;

-- Add unique constraints (PostgreSQL doesn't support IF NOT EXISTS for constraints)
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'classes_slug_unique'
    ) THEN
        ALTER TABLE encyclopedia.classes ADD CONSTRAINT classes_slug_unique UNIQUE (slug);
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'species_slug_unique'
    ) THEN
        ALTER TABLE encyclopedia.species ADD CONSTRAINT species_slug_unique UNIQUE (slug);
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'breeds_slug_unique'
    ) THEN
        ALTER TABLE encyclopedia.breeds ADD CONSTRAINT breeds_slug_unique UNIQUE (slug);
    END IF;
END $$;

-- Create indexes for faster slug lookups
CREATE INDEX IF NOT EXISTS idx_classes_slug ON encyclopedia.classes(slug);
CREATE INDEX IF NOT EXISTS idx_species_slug ON encyclopedia.species(slug);
CREATE INDEX IF NOT EXISTS idx_breeds_slug ON encyclopedia.breeds(slug);

