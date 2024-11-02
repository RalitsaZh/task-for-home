CREATE TABLE IF NOT EXISTS mfa_codes
(
    id                NUMERIC,
    expiration_time   TIMESTAMP NOT NULL,
    email             VARCHAR,
    verification_code VARCHAR,
    is_code_used      BOOLEAN DEFAULT FALSE,
    PRIMARY KEY (id, expiration_time)
);
CREATE SEQUENCE IF NOT EXISTS mfa_codes_seq START WITH 1;