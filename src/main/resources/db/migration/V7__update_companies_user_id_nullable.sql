-- V7: Update companies table to allow nullable user_id
-- This allows companies to be created without an assigned user initially

-- Make user_id nullable in companies table
ALTER TABLE companies ALTER COLUMN user_id DROP NOT NULL; 