(ns items2.db.mail-list
  (:require [items2.db.core :as db]
            [hodur-translate.spec.malli-schemas :refer [local-date local-date-time]]
            [aave.core :refer [>defn >defn-]]
            [honeysql.core :as sql]
            [honeysql.helpers :as sqlh]
            [honeysql-postgres.helpers :as psqlh]
            [java-time :as jt]
            [items2.utils :as utils]))

(defn get-mail-list
  ([db]
   (let [sql-map (-> (sql/build :select [:mail-list/unit :mail-list/subunit :mail-list/email]
                                :from :mail-list)
                     (sqlh/order-by [:mail-list/unit :nulls-first]
                                    [:mail-list/subunit :nulls-first]))]
     (tap> sql-map)
     (tap> (sql/format sql-map))
     (db/honey! db sql-map {})))
  ([]
   (get-mail-list @db/sys-db)))