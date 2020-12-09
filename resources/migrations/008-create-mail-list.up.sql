CREATE TABLE mail_list (
  email text NOT NULL,
  id bigint NOT NULL GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  memo text,
  name text,
  position text,
  subunit text,
  unit text NOT NULL
);