(ns items2.db.items-child
  (:require [items2.db.core :as db]
            [aave.core :refer [>defn >defn-]]
            [clojure.spec.alpha :as s]
            [honeysql.core :as sql]
            [honeysql.helpers :as sqlh]
            [malli.core :as m]
            [malli.error :as me]
            [malli.transform :as mt]
            [items2.items-malli :as im]
            [items2.json :as j]
            [items2.utils :as utils]
            [taoensso.timbre :as timbre]))

(def all-list-schema
  (utils/optional-id-schema (:all-list im/items-malli)))

(def item-people-schema
  (utils/optional-id-schema (:item-people im/items-malli)))

(def item-list-schema
  (utils/optional-id-schema (:item-list im/items-malli)))

(>defn delete-table-by-items-id!
  ([db items-id tables]
   [db/malli-db pos-int? [:sequential keyword?] => any?]
   (let [sql-fn (fn [t]
                  (sql/build :delete-from t :where [:= :items-id items-id]))
         sql-maps (map sql-fn tables)]
     (dorun (map #(db/honey! db % {}) sql-maps))))
  ([items-id tables]
   [pos-int? [:sequential keyword?] => any?]
   (delete-table-by-items-id! @db/sys-db items-id tables)))

(>defn merge-items-id
  [from to]
  [map? seq? => map?])

(>defn insert-table-by-items-id!
  ([db items-id rows table]
   [db/malli-db pos-int? seq? keyword? => any?]
   (let [values (map #(assoc % :items-id items-id) rows)
         sql-map (-> (sqlh/insert-into table)
                     (sqlh/values values))]
     (db/honey! db sql-map {})))
  ([items-id rows table]
   [pos-int? seq? keyword? => any?]
   (insert-table-by-items-id! @db/sys-db items-id rows table)))
