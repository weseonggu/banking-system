-- init.sql

-- 'auth' 데이터베이스 생성
CREATE DATABASE auth
  WITH OWNER = banking
  ENCODING = 'UTF8'
  LC_COLLATE = 'en_US.utf8'
  LC_CTYPE = 'en_US.utf8'
  TABLESPACE = pg_default
  CONNECTION LIMIT = -1;

-- 'account' 데이터베이스 생성
CREATE DATABASE account
  WITH OWNER = banking
  ENCODING = 'UTF8'
  LC_COLLATE = 'en_US.utf8'
  LC_CTYPE = 'en_US.utf8'
  TABLESPACE = pg_default
  CONNECTION LIMIT = -1;

-- 'personal' 데이터베이스 생성
CREATE DATABASE personal
  WITH OWNER = banking
  ENCODING = 'UTF8'
  LC_COLLATE = 'en_US.utf8'
  LC_CTYPE = 'en_US.utf8'
  TABLESPACE = pg_default
  CONNECTION LIMIT = -1;

-- 'product' 데이터베이스 생성
CREATE DATABASE product
  WITH OWNER = banking
  ENCODING = 'UTF8'
  LC_COLLATE = 'en_US.utf8'
  LC_CTYPE = 'en_US.utf8'
  TABLESPACE = pg_default
  CONNECTION LIMIT = -1;

-- 'support' 데이터베이스 생성
CREATE DATABASE support
  WITH OWNER = banking
  ENCODING = 'UTF8'
  LC_COLLATE = 'en_US.utf8'
  LC_CTYPE = 'en_US.utf8'
  TABLESPACE = pg_default
  CONNECTION LIMIT = -1;

-- 'notify' 데이터베이스 생성
CREATE DATABASE notify
  WITH OWNER = banking
  ENCODING = 'UTF8'
  LC_COLLATE = 'en_US.utf8'
  LC_CTYPE = 'en_US.utf8'
  TABLESPACE = pg_default
  CONNECTION LIMIT = -1;
