-- Drop foreign key constraints and tables related to roles
-- Since we're now using Keycloak for role management

-- Drop user_roles junction table first (due to foreign key constraints)
DROP TABLE IF EXISTS user_roles CASCADE;

-- Drop roles table
DROP TABLE IF EXISTS roles CASCADE;

-- Note: Roles are now managed entirely in Keycloak
-- Spring Security will read roles from JWT tokens issued by Keycloak 