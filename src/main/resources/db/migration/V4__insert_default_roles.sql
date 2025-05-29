-- Insert default roles if not exists
INSERT INTO roles (name, description, created_at, updated_at) 
VALUES 
    ('ADMIN', 'Administrator with full system access', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('EMPLOYER', 'Employer who can post jobs and manage applications', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('CANDIDATE', 'Job seeker who can apply for jobs', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (name) DO UPDATE 
SET 
    description = EXCLUDED.description,
    updated_at = CURRENT_TIMESTAMP; 