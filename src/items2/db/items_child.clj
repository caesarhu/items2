(ns items2.db.items-child
  (:require
    [aave.core :refer [>defn >defn-]]
    [honeysql.core :as sql]
    [honeysql.helpers :as sqlh]
    [items2.db.core :as db]
    [items2.utils :as utils]
    [medley.core :as medley]))


(>defn delete-table-by-items-id!
       ([db items-id tables]
        [db/malli-db pos-int? [:sequential keyword?] => any?]
        (let [sql-fn (fn [t]
                       (sql/build :delete-from t :where [:= :items-id items-id]))
              sql-maps (map sql-fn tables)]
          (mapv #(db/honey! db % {}) sql-maps)))
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
       [map? map? [:sequential keyword?] => [:sequential [:tuple keyword? [:sequential [:map-of keyword? any?]]]]]
       (let [items-id (find-items-id upserted-item)
             merge-fn (fn [table]
                        (let [values (get item-raw table)
                              id-values (map #(assoc % (utils/qualify-key table :items-id) items-id) values)]
                          [table id-values]))]
         (map merge-fn children)))


(>defn insert-items-child!
       ([db child]
        [db/malli-db coll? => any?]
        (let [[table values] child
              sql-map (-> (sqlh/insert-into table)
                          (sqlh/values values))]
          (when (not-empty values)
            (db/honey! db sql-map {}))))
       ([child]
        [coll? => any?]
        (insert-items-child! @db/sys-db child)))

