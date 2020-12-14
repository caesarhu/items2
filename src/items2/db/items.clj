(ns items2.db.items
  (:require [items2.db.core :as db]
            [honeysql.core :as sql]
            [honeysql.helpers :as sqlh]
            [malli.core :as m]
            [malli.transform :as mt]
            [items2.json :as json]))

(defn upsert-item
  ([db item]
   (if-let [good-item (when (m/validate json/item-schema item)
                        (m/decode json/item-schema item mt/strip-extra-keys-transformer))]
     (let [sql-map (db/upsert-one :items item :file)]
       sql-map)
     (println "data bad!"))))