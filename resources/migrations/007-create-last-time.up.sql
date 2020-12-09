CREATE TABLE last_time (
  fail bigint NOT NULL,
  file_time timestamp NOT NULL,
  id bigint NOT NULL GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  success bigint NOT NULL,
  total bigint NOT NULL
);