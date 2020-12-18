(ns items2.db.stats
  (:require [items2.db.core :as db]
            [hodur-translate.spec.malli-schemas :refer [local-date local-date-time]]
            [aave.core :refer [>defn >defn-]]
            [honeysql.core :as sql]
            [honeysql.helpers :as sqlh]
            [honeysql-postgres.helpers :as psqlh]
            [java-time :as jt]))

(def malli-period
  [:map
   [:start-date local-date]
   [:end-date local-date]])

(defn item-list-query
  ([db items-id]
   (let [sql-map (-> (sql/build :select :item-list.items-id
                                :from :item-list)
                     (sqlh/merge-select (sql/call :concat :item-list.kind :item-list.subkind :item-list.object))
                     (sqlh/where [:= :item-list.items-id items-id]))]
     (tap> sql-map)
     (tap> (sql/format sql-map))
     (db/honey db sql-map {})))
  ([items-id]
   (item-list-query @db/sys-db items-id)))

(>defn items-period-record
  ([db period]
   [db/malli-db malli-period => any?]
   (let [start-date (:start-date period)
         end-date (jt/plus (:end-date period) (jt/days 1))
         sql-map (-> (sql/build :select :items.* :from :items)
                     (sqlh/left-join :item-list [:= :items.id :item-list.items-id])
                     (sqlh/where [:and
                                  [:>= :items.check-time start-date]
                                  [:< :items.check-time end-date]])
                     (sqlh/order-by :items.unit :items.subunit :items.police :items.check-time))]
     (tap> (sql/format sql-map))
     (db/honey db sql-map {})))
  ([period]
   [malli-period => any?]
   (items-period-record @db/sys-db period)))


