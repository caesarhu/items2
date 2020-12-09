CREATE TABLE all_list (
  id bigint NOT NULL GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  item text NOT NULL,
  items_id bigint NOT NULL REFERENCES items ON DELETE cascade ON UPDATE cascade,
  quantity bigint NOT NULL
);