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
            [taoensso.timbre :as timbre]
            [medley.core :as medley]))

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

(>defn find-items-id
  [upserted-item]
  [map? => pos-int?]
  (-> (medley/find-first #(= "id" (name (key %))) upserted-item)
      val))

(>defn merge-items-id
  [item-raw upserted-item children]
  [map? map? [:sequential keyword?] => [:sequential vector?]]
  (let [items-id (find-items-id upserted-item)
        merge-fn (fn [table]
                   (let [values (get item-raw table)
                         id-values (map #(assoc % :items-id items-id) values)]
                     [table id-values]))]
    (map merge-fn children)))

(>defn insert-items-child!
  ([db child]
   [db/malli-db vector? => any?]
   (let [[table values] child
         sql-map (-> (sqlh/insert-into table)
                     (sqlh/values values))]
     (db/honey! db sql-map {})))
  ([child]
   [vector? => any?]
   (insert-items-child! @db/sys-db child)))

