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

(>defn upsert-item-and-child!
  ([db item]
   [db/malli-db map? => any?]
   (let [result (upsert-item! db item)
         items-id (:items/id result)
         child-tables [:all-list :item-list :item-people]
         child-map (select-keys item child-tables)
         _ (child/delete-table-by-items-id! db items-id child-tables)
         args-v (map #(vector db items-id (val %) (key %)) child-map)]
     (dorun
       (for [v args-v]
         (apply child/insert-table-by-items-id! v)))
     result))
  ([item]
   [map? => any?]
   (upsert-item-and-child! @db/sys-db item)))