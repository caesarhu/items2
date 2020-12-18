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

(def item-list-subquery
  (-> (sqlh/select :ilist2/items-id [(sql/call :string_agg :concat ", ") :項目清單])
      (sqlh/from [(-> (sql/build :select :item-list/items-id
                                 :from :item-list)
                      (sqlh/merge-select (sql/call :concat :item-list/kind "-" :item-list/subkind "-" :item-list/object))) :ilist2])
      (sqlh/group :items-id)))

(def item-people-subquery
  (-> (sqlh/select :people2/items-id [(sql/call :string_agg :concat ", ") :件數人數])
      (sqlh/from [(-> (sql/build :select :item-people/items-id
                                 :from :item-people)
                      (sqlh/merge-select (sql/call :concat :item-people/kind "-" :item-people/piece "-" :item-people/people))) :people2])
      (sqlh/group :items-id)))

(def all-list-subquery
  (-> (sqlh/select :alist2/items-id [(sql/call :string_agg :concat ", ") :所有項目數量])
      (sqlh/from [(-> (sql/build :select :all-list/items-id
                                 :from :all-list)
                      (sqlh/merge-select (sql/call :concat :all-list/item "-" :all-list/quantity))) :alist2])
      (sqlh/group :items-id)))

(>defn items-period-record
  ([db period]
   [db/malli-db malli-period => any?]
   (let [start-date (:start-date period)
         end-date (jt/plus (:end-date period) (jt/days 1))
         sql-map (-> (sql/build :select :items/* :from :items)
                     (sqlh/merge-select :ilist/項目清單 :people/件數人數 :alist/所有項目數量)
                     (sqlh/left-join [item-list-subquery :ilist] [:= :items/id :ilist/items-id]
                                     [item-people-subquery :people] [:= :items/id :people/items-id]
                                     [all-list-subquery :alist] [:= :items/id :alist/items-id])
                     (sqlh/where [:and
                                  [:>= :items/check-time start-date]
                                  [:< :items/check-time end-date]])
                     (sqlh/order-by :items/unit :items/subunit :items/police :items/check-time))]
     (db/honey db sql-map {})))
  ([period]
   [malli-period => any?]
   (items-period-record @db/sys-db period)))

(>defn stats-period
  ([db period]
   [db/malli-db malli-period => any?]
   (let [start-date (:start-date period)
         end-date (jt/plus (:end-date period) (jt/days 1))
         sql-map (-> (sqlh/select :items/unit :items/subunit :items/police :item-list/kind :item-list/subkind [:%count.item-list.subkind :合計])
                     (sqlh/from :items :item-list)
                     (sqlh/where [:and
                                  [:>= :items/check-time start-date]
                                  [:< :items/check-time end-date]])
                     (sqlh/group (sql/call :rollup :unit :subunit  :police :kind :subkind)))]
     (tap> (sql/format sql-map :namespace-as-table? true))
     (db/honey db sql-map {})))
  ([period]
   [malli-period => any?]
   (stats-period @db/sys-db period)))

