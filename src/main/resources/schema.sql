CREATE TABLE IF NOT EXISTS users
(
  id                UUID PRIMARY KEY,
  first_name        VARCHAR(50)              NOT NULL,
  last_name         VARCHAR(50)              NOT NULL,
  email             VARCHAR(320)             NOT NULL,
  state             VARCHAR(50)              NOT NULL,
  registration_date TIMESTAMP WITH TIME ZONE NOT NULL,
  modified          TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE UNIQUE INDEX IF NOT EXISTS email_unique_idx ON users (UPPER(email));
