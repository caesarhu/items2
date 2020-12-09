CREATE TABLE ipad (
  id bigint NOT NULL GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  ip text NOT NULL,
  ipad_name text NOT NULL,
  subunit text,
  unit text NOT NULL
);