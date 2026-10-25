INSERT INTO users (email,password,role,enabled)
VALUES ('applicant@demo.com','$2a$10$wN1eK4O5eS1a1jM9jI6qAeeL/aXyR0k4Zp/n5I6k8m.U.1hO3X5z2','APPLICANT',true)
ON CONFLICT (email) DO UPDATE
SET password = EXCLUDED.password,
    role     = EXCLUDED.role,
    enabled  = EXCLUDED.enabled;
