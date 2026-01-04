-- Add weight and height columns to pets table
-- These fields are nullable to support existing data

ALTER TABLE pet.pets
    ADD COLUMN weight NUMERIC,
    ADD COLUMN height NUMERIC;

-- No NOT NULL constraints to ensure backward compatibility with existing data
