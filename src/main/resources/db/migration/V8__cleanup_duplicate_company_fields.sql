-- V8: Cleanup duplicate fields in companies table
-- Remove duplicate social network and contact information fields

-- Drop duplicate social network columns
ALTER TABLE companies DROP COLUMN IF EXISTS social_facebook;
ALTER TABLE companies DROP COLUMN IF EXISTS social_twitter;
ALTER TABLE companies DROP COLUMN IF EXISTS social_linkedin;

-- Drop duplicate contact information columns
ALTER TABLE companies DROP COLUMN IF EXISTS contact_phone;
ALTER TABLE companies DROP COLUMN IF EXISTS contact_email;
ALTER TABLE companies DROP COLUMN IF EXISTS contact_website;
ALTER TABLE companies DROP COLUMN IF EXISTS contact_address; 