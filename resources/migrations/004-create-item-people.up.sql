CREATE TABLE item_people (
  id bigint NOT NULL GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  items_id bigint NOT NULL REFERENCES items ON DELETE cascade ON UPDATE cascade,
  kind text NOT NULL,
  people bigint,
  piece bigint NOT NULL
);
--;;
CREATE INDEX idx_item_people_by_items_id ON item_people (items_id);