(ns items2.db.items
  (:require [items2.config :as config]
            [exoscale.ex :as ex]
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
            [items2.items-malli :as im]
            [items2.db.items-child :as child]
            [datoteka.core :as fs]))

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

(>defn upsert-item-and-children!
  ([db item-and-children]
   [db/malli-db j/parsed-item-schema => map?]
   (ex/try+
     (when-let [upserted (upsert-item! db (:item item-and-children))]
       (let [tables (:items-child @config/config)
             children (child/merge-items-id item-and-children upserted tables)]
         (child/delete-table-by-items-id! db (child/find-items-id upserted) tables)
         (mapv child/insert-items-child! children)
         upserted))
     (catch Throwable e
       (timbre/log :error
                   ::upsert-item-and-children!
                   (utils/ex-cause-and-msg e)
                   {:item item-and-children
                    :from ::upsert-item-and-children!})
       (throw (ex/ex-info (utils/ex-cause-and-msg e)
                          ::ex/fault
                          {:from ::upsert-item-and-children!}
                          e)))))
  ([item-and-children]
   [j/parsed-item-schema => map?]
   (upsert-item-and-children! @db/sys-db item-and-children)))

(>defn import-item-files!
  ([db json-files]
   [db/malli-db [:sequential [:fn #(fs/file? %)]] => any?]
   (let [import-fn (fn [json-file]
                     (->> json-file
                          j/json-parser
                          (upsert-item-and-children! db)))]
     (dorun
       (map import-fn json-files))))
  ([json-files]
   [[:sequential [:fn #(fs/file? %)]]  => any?]
   (import-item-files! @db/sys-db json-files)))

(defn max-file-time
  ([db]
   (-> (db/honey-one! db (sql/build :select :%max.file_time :from :items) {})
       :max))
  ([]
   (max-file-time @db/sys-db)))