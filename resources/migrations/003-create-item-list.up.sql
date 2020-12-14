CREATE TABLE item_list (
  id bigint NOT NULL GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  items_id bigint NOT NULL REFERENCES items ON DELETE cascade ON UPDATE cascade,
  kind text NOT NULL,
  object text,
  subkind text
);
--;;
CREATE INDEX idx_item_list_by_items_id ON item_list (items_id);