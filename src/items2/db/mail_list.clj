(ns items2.db.mail-list
  (:require
    [aave.core :refer [>defn >defn-]]
    [hodur-translate.spec.malli-schemas :refer [local-date local-date-time]]
    [honeysql-postgres.helpers :as psqlh]
    [honeysql.core :as sql]
    [honeysql.helpers :as sqlh]
    [items2.db.core :as db]
    [items2.utils :as utils]
    [java-time :as jt]))


(defn get-mail-list
  ([db]
   (let [sql-map (-> (sqlh/select :mail-list/unit :mail-list/subunit
                                  :mail-list/email :mail-list/whole)
                     (sqlh/from :mail-list)
                     (sqlh/order-by :mail-list/unit
                                    :mail-list/subunit
                                    :mail-list/email))]
     (->> (db/honey! db sql-map {})
          (map utils/unqualify-map)
          (group-by (fn [m]
                      (if (:whole m)
                        "全局"
                        (str (:unit m) (:subunit m))))))))
  ([]
   (get-mail-list @db/sys-db)))
