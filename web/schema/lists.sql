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

-- Session storage; should remain independent of other tables for eventual move.
CREATE TABLE session (
  key TEXT NOT NULL PRIMARY KEY,
  data TEXT NOT NULL,
  last_accessed TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
  created TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE OR REPLACE FUNCTION session_upsert(session_key TEXT, new_data TEXT) RETURNS VOID AS
$$
BEGIN
    LOOP
        -- first try to update the key
        UPDATE session
        SET data = new_data,
            last_accessed = CURRENT_TIMESTAMP 
        WHERE key = session_key;
        IF found THEN
            RETURN;
        END IF;
        -- not there, so try to insert the key
        -- if someone else inserts the same key concurrently,
        -- we could get a unique-key failure
        BEGIN
            INSERT INTO session(key, data) VALUES (session_key, new_data);
            RETURN;
        EXCEPTION WHEN unique_violation THEN
            -- do nothing, and loop to try the UPDATE again
        END;
    END LOOP;
END;
$$
LANGUAGE plpgsql;

COMMIT;

