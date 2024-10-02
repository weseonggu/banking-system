CREATE UNIQUE INDEX unique_custom_username_idx ON p_customer (username) WHERE is_delete = false;
CREATE UNIQUE INDEX unique_custom_email_idx ON p_customer (email) WHERE is_delete = false;
CREATE UNIQUE INDEX unique_custom_phone_number_idx ON p_customer (phone_number) WHERE is_delete = false;

CREATE UNIQUE INDEX unique_employee_username_idx ON p_employee (username) WHERE is_delete = false;
CREATE UNIQUE INDEX unique_employee_email_idx ON p_employee (email) WHERE is_delete = false;
CREATE UNIQUE INDEX unique_employee_phone_number_idx ON p_employee (phone_number) WHERE is_delete = false;
