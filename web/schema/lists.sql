BEGIN;

-- wanted to call this user, but that's a reserved word
CREATE TABLE account (
  id BIGSERIAL NOT NULL PRIMARY KEY,
  username TEXT NOT NULL,
  password_hash TEXT,  -- yes, can be null (for low-friction sign up)
  email TEXT,
  created TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE list (
  id BIGSERIAL NOT NULL PRIMARY KEY,
  label TEXT NULL,
  description TEXT NULL
);

CREATE TABLE list_item (
  id BIGSERIAL NOT NULL PRIMARY KEY,
  list_id BIGINT NOT NULL REFERENCES list(id),
  label TEXT NULL,
  url TEXT NULL
);

COMMIT;

