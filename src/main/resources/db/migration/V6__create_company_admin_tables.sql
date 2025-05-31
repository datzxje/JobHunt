-- V6: Create Company Admin Tables

-- Add additional fields to companies table
ALTER TABLE companies 
ADD COLUMN admin_user_id BIGINT REFERENCES users(id),
ADD COLUMN social_facebook VARCHAR(255),
ADD COLUMN social_twitter VARCHAR(255),
ADD COLUMN social_linkedin VARCHAR(255),
ADD COLUMN social_instagram VARCHAR(255),
ADD COLUMN contact_phone VARCHAR(50),
ADD COLUMN contact_email VARCHAR(255),
ADD COLUMN contact_website VARCHAR(255),
ADD COLUMN contact_address TEXT;

-- Create ENUM types
CREATE TYPE request_status AS ENUM ('PENDING', 'APPROVED', 'REJECTED');
CREATE TYPE member_role AS ENUM ('ADMIN', 'HR');
CREATE TYPE member_status AS ENUM ('ACTIVE', 'INACTIVE');

-- Create company_join_requests table
CREATE TABLE company_join_requests (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    company_id BIGINT NOT NULL REFERENCES companies(id) ON DELETE CASCADE,
    message TEXT,
    status request_status DEFAULT 'PENDING',
    requested_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    reviewed_at TIMESTAMP NULL,
    reviewed_by BIGINT REFERENCES users(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create company_members table
CREATE TABLE company_members (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    company_id BIGINT NOT NULL REFERENCES companies(id) ON DELETE CASCADE,
    role member_role DEFAULT 'HR',
    department VARCHAR(100) DEFAULT 'Human Resources',
    status member_status DEFAULT 'ACTIVE',
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT unique_user_company UNIQUE (user_id, company_id)
);

-- Add posted_by field to jobs table
ALTER TABLE jobs 
ADD COLUMN posted_by BIGINT REFERENCES users(id);

-- Create indexes for better performance
CREATE INDEX idx_company_join_requests_company_id ON company_join_requests(company_id);
CREATE INDEX idx_company_join_requests_status ON company_join_requests(status);
CREATE INDEX idx_company_join_requests_user_id ON company_join_requests(user_id);

CREATE INDEX idx_company_members_company_id ON company_members(company_id);
CREATE INDEX idx_company_members_user_id ON company_members(user_id);
CREATE INDEX idx_company_members_role ON company_members(role);
CREATE INDEX idx_company_members_status ON company_members(status);

CREATE INDEX idx_jobs_company_id ON jobs(company_id);
CREATE INDEX idx_jobs_posted_by ON jobs(posted_by);

-- Create update timestamp trigger function
CREATE OR REPLACE FUNCTION update_modified_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Create triggers for automatic timestamp updates
CREATE TRIGGER update_companies_modtime 
    BEFORE UPDATE ON companies 
    FOR EACH ROW EXECUTE FUNCTION update_modified_column();

CREATE TRIGGER update_company_join_requests_modtime 
    BEFORE UPDATE ON company_join_requests 
    FOR EACH ROW EXECUTE FUNCTION update_modified_column();

CREATE TRIGGER update_company_members_modtime 
    BEFORE UPDATE ON company_members 
    FOR EACH ROW EXECUTE FUNCTION update_modified_column(); 