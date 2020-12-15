(ns items2.db.items
  (:require [items2.config :as config]
            [items2.db.core :as db]
            [aave.core :refer [>defn >defn-]]
            [honeysql.core :as sql]
            [honeysql.helpers :as sqlh]
            [malli.core :as m]
            [malli.error :as me]
            [malli.transform :as mt]
            [items2.json :as j]
            [items2.utils :as utils]
            [taoensso.timbre :as timbre]
            [items2.db.items-child :as child]))

(>defn upsert-item!
  ([db item]
   [db/malli-db map? => any?]
   (if-let [good-item (when (m/validate j/item-schema item)
                        (m/decode j/item-schema item mt/strip-extra-keys-transformer))]
     (let [sql-map (db/upsert-one :items good-item :file)]
       (db/honey-one! db sql-map {}))
     (utils/validate-throw j/item-schema item)))
  ([item]
   [map? => any?]
   (upsert-item! @db/sys-db item)))

(defn upsert-item-and-child!
  ([db item]
   (let [result (upsert-item! db item)
         items-id (:items/id result)
         child-tables (:items-child @config/config)
         child-map (select-keys item child-tables)
         child-insert (fn [entry]
                        (let [table (key entry)
                              values (val entry)]
                          (child/insert-table-by-items-id! db items-id values table)))]
     (child/delete-table-by-items-id! db items-id child-tables)
     (mapv child-insert child-map)
     result))
  ([item]
   (upsert-item-and-child! @db/sys-db item)))