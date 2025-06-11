-- Initialize Job Categories (Specialisms)
INSERT INTO job_categories (name, description, active, created_at, updated_at) VALUES
('Banking', 'Banking and Financial Services', true, NOW(), NOW()),
('Digital & Creative', 'Digital and Creative Industries', true, NOW(), NOW()),
('Retail', 'Retail and Consumer Services', true, NOW(), NOW()),
('Human Resources', 'Human Resources and Talent Management', true, NOW(), NOW()),
('Management', 'Management and Leadership', true, NOW(), NOW()),
('Accounting & Finance', 'Accounting and Finance', true, NOW(), NOW()),
('Digital', 'Digital Technology', true, NOW(), NOW()),
('Creative Art', 'Creative Arts and Design', true, NOW(), NOW()),
('FinTech', 'Financial Technology', true, NOW(), NOW()),
('Technology', 'Information Technology', true, NOW(), NOW()),
('Engineering', 'Engineering and Technical', true, NOW(), NOW()),
('Product Design', 'Product Design and Development', true, NOW(), NOW()),
('Healthcare', 'Healthcare and Medical', true, NOW(), NOW()),
('Education', 'Education and Training', true, NOW(), NOW());

-- Initialize Skills
INSERT INTO skills (name, description, active, created_at, updated_at) VALUES
('JavaScript', 'JavaScript programming language', true, NOW(), NOW()),
('React', 'React.js framework', true, NOW(), NOW()),
('Node.js', 'Node.js runtime environment', true, NOW(), NOW()),
('Python', 'Python programming language', true, NOW(), NOW()),
('Java', 'Java programming language', true, NOW(), NOW()),
('PHP', 'PHP programming language', true, NOW(), NOW()),
('Angular', 'Angular framework', true, NOW(), NOW()),
('Vue.js', 'Vue.js framework', true, NOW(), NOW()),
('CSS', 'Cascading Style Sheets', true, NOW(), NOW()),
('HTML', 'HyperText Markup Language', true, NOW(), NOW()),
('SQL', 'Structured Query Language', true, NOW(), NOW()),
('MongoDB', 'MongoDB database', true, NOW(), NOW()),
('Design', 'General design skills', true, NOW(), NOW()),
('Photoshop', 'Adobe Photoshop', true, NOW(), NOW()),
('Sketch', 'Sketch design tool', true, NOW(), NOW()),
('InVision', 'InVision prototyping tool', true, NOW(), NOW()),
('Framer X', 'Framer X design tool', true, NOW(), NOW()),
('Jira', 'Jira project management', true, NOW(), NOW()),
('Confluence', 'Confluence documentation', true, NOW(), NOW()),
('UX Design', 'User Experience Design', true, NOW(), NOW()),
('Product Management', 'Product Management skills', true, NOW(), NOW()),
('Agile', 'Agile methodology', true, NOW(), NOW()),
('Marketing', 'Marketing and promotion', true, NOW(), NOW()),
('Sales', 'Sales and business development', true, NOW(), NOW());

-- Initialize Languages
INSERT INTO languages (name, iso_code, active, created_at, updated_at) VALUES
('English', 'en', true, NOW(), NOW()),
('Vietnamese', 'vi', true, NOW(), NOW()),
('Chinese', 'zh', true, NOW(), NOW()),
('Japanese', 'ja', true, NOW(), NOW()),
('Korean', 'ko', true, NOW(), NOW()),
('French', 'fr', true, NOW(), NOW()),
('German', 'de', true, NOW(), NOW()),
('Spanish', 'es', true, NOW(), NOW()); 