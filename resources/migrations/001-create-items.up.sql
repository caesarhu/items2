CREATE TABLE items (
  carry text,
  check_line text,
  check_sign text,
  check_time timestamp NOT NULL,
  file text NOT NULL UNIQUE,
  file_time timestamp NOT NULL,
  flight text,
  id bigint NOT NULL GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  ip text,
  memo text,
  passenger_id text,
  passenger_sign text,
  police text NOT NULL,
  process text,
  subunit text,
  trader_sign text,
  unit text NOT NULL
);
--;;
CREATE INDEX idx_items_by_unit ON items (unit, subunit, police, check_time);
--;;
CREATE INDEX idx_items_by_file_time ON items (file_time);