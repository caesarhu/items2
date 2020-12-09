CREATE TABLE item_list (
  id bigint NOT NULL GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  items_id bigint NOT NULL REFERENCES items ON DELETE cascade ON UPDATE cascade,
  kind text NOT NULL,
  object text,
  subkind text
);