INSERT INTO users (email,password,role,enabled)
VALUES ('applicant@demo.com','','APPLICANT',true)
ON CONFLICT (email) DO UPDATE
SET password = EXCLUDED.password,
    role     = EXCLUDED.role,
    enabled  = EXCLUDED.enabled;
