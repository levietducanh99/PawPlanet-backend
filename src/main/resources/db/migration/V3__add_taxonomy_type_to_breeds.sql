-- Migration: Add taxonomy_type column to encyclopedia.breeds
-- Purpose: Distinguish between breed (cultivated variety) and subspecies (scientific classification)
-- Date: 2025-12-30

ALTER TABLE encyclopedia.breeds
ADD COLUMN taxonomy_type VARCHAR(20) NOT NULL DEFAULT 'breed';

COMMENT ON COLUMN encyclopedia.breeds.taxonomy_type IS 'Type of taxonomic classification: breed (cultivated variety) or subspecies (scientific classification)';

