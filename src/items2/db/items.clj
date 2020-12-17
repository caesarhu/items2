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
            [datoteka.core :as fs]
            [next.jdbc :as jdbc]
            [java-time :as jt]))

(>defn upsert-item!
  ([db item opts]
   [db/malli-db j/item-schema db/malli-db-opts => map?]
   (when-let [good-item (when (m/validate j/item-schema item)
                          (m/decode j/item-schema item mt/strip-extra-keys-transformer))]
     (let [sql-map (db/upsert-one :items good-item :file)]
       (db/honey-one! db sql-map opts))))
  ([item opts]
   [j/item-schema db/malli-db-opts => map?]
   (upsert-item! @db/sys-db item opts))
  ([item]
   [j/item-schema => map?]
   (upsert-item! @db/sys-db item {})))

(>defn upsert-item-and-children!
  ([db item-and-children]
   [db/malli-db j/parsed-item-schema => map?]
   (jdbc/with-transaction [tx db]
     (when-let [upserted (upsert-item! tx (:item item-and-children) db/auto-opts)]
       (let [tables (:items-child @config/config)
             children (child/merge-items-id item-and-children upserted tables)]
         (child/delete-table-by-items-id! tx (child/find-items-id upserted) tables)
         (mapv #(child/insert-items-child! tx %) children)
         upserted))))
  ([item-and-children]
   [j/parsed-item-schema => map?]
   (upsert-item-and-children! @db/sys-db item-and-children)))

(def string-or-file
  [:or string? [:fn #(fs/file? %)]])

(>defn import-item-file!
  ([db json-file]
   [db/malli-db string-or-file => any?]
   (ex/try+
     (->> json-file
          j/json-parser
          (upsert-item-and-children! db))
     (catch Throwable e
       (timbre/log :error
                   (utils/ex-cause-and-msg e)
                   {:json-file json-file
                    :from ::import-item-file!
                    :info "發生錯誤，忽略錯誤繼續執行!"}))))
  ([json-file]
   [string-or-file => any?]
   (import-item-file! @db/sys-db json-file)))

(>defn import-item-files!
  ([db json-files]
   [db/malli-db [:sequential string-or-file] => nil?]
   (dorun
     (map #(import-item-file! db %) json-files)))
  ([json-files]
   [[:sequential string-or-file]  => nil?]
   (import-item-files! @db/sys-db json-files)))

(defn max-file-time
  ([db]
   (or (-> (db/honey-one! db (sql/build :select :%max.file_time :from :items) {})
           :max)
       (jt/local-date-time 1 1 1)))
  ([]
   (max-file-time @db/sys-db)))