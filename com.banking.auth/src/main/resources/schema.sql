CREATE UNIQUE INDEX unique_username_idx ON p_customer (username) WHERE is_delete = false;
CREATE UNIQUE INDEX unique_email_idx ON p_customer (email) WHERE is_delete = false;
CREATE UNIQUE INDEX unique_phone_number_idx ON p_customer (phone_number) WHERE is_delete = false;
