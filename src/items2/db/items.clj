(ns items2.db.items
  (:require [items2.db.core :as db]
            [aave.core :refer [>defn >defn-]]
            [honeysql.core :as sql]
            [honeysql.helpers :as sqlh]
            [malli.core :as m]
            [malli.error :as me]
            [malli.transform :as mt]
            [items2.json :as j]
            [items2.utils :as utils]
            [taoensso.timbre :as timbre]))

(>defn upsert-item!
  ([db item]
   [db/malli-db map? => [:or map? nil?]]
   (if-let [good-item (when (m/validate j/item-schema item)
                        (m/decode j/item-schema item mt/strip-extra-keys-transformer))]
     (let [sql-map (db/upsert-one :items good-item :file)]
       (db/honey-one! db sql-map {}))
     (timbre/log :error ::upsert-item (-> (m/explain j/item-schema item) me/humanize))))
  ([item]
   [map? => [:or map? nil?]]
   (upsert-item! @db/sys-db item)))