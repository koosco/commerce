ALTER TABLE user_auth
    ADD CONSTRAINT uq_email_provider UNIQUE (email, provider);
