CREATE TABLE units (
  id bigint NOT NULL GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  subunit text,
  unit text NOT NULL
);