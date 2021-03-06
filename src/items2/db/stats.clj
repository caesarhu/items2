(ns items2.db.stats
  (:require
    [aave.core :refer [>defn >defn-]]
    [hodur-translate.spec.malli-schemas :refer [local-date local-date-time]]
    [honeysql.core :as sql]
    [honeysql.helpers :as sqlh]
    [items2.db.core :as db]
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


(>defn items-period-detail
       ([db period unit subunit]
        [db/malli-db malli-period [:or string? nil?] [:or string? nil?] => any?]
        (let [start-date (:start-date period)
              end-date (jt/plus (:end-date period) (jt/days 1))
              sql-map-base (-> (sql/build :select :*
                                          :from :items)
                               (sqlh/merge-select :ilist/項目清單 :people/件數人數 :alist/所有項目數量)
                               (sqlh/left-join [item-list-subquery :ilist] [:= :items/id :ilist/items-id]
                                               [item-people-subquery :people] [:= :items/id :people/items-id]
                                               [all-list-subquery :alist] [:= :items/id :alist/items-id])
                               (sqlh/where [:and
                                            [:>= :items/check-time start-date]
                                            [:< :items/check-time end-date]])
                               (sqlh/order-by :items/unit :items/subunit :items/police :items/check-time))
              sql-map (cond-> sql-map-base
                        unit (sqlh/merge-where [:= :items/unit unit])
                        subunit (sqlh/merge-where [:= :items/subunit subunit]))]
          (db/honey! db sql-map {})))
       ([db period unit]
        [db/malli-db malli-period [:or string? nil?] => any?]
        (items-period-detail db period unit nil))
       ([db period]
        [db/malli-db malli-period => any?]
        (items-period-detail db period nil nil))
       ([period]
        [malli-period => any?]
        (items-period-detail @db/sys-db period nil nil)))


(>defn items-period-stats
       ([db period]
        [db/malli-db malli-period => any?]
        (let [start-date (:start-date period)
              end-date (jt/plus (:end-date period) (jt/days 1))
              sql-map (-> (sqlh/select :items/unit :items/subunit
                                       :items/police
                                       :item-list/kind :item-list/subkind
                                       [:%count.item-list.subkind :合計]
                                       [start-date :開始日期]
                                       [end-date :結束日期])
                          (sqlh/from :items :item-list)
                          (sqlh/where [:and
                                       [:= :item-list/items-id :items/id]
                                       [:>= :items/check-time start-date]
                                       [:< :items/check-time end-date]])
                          (sqlh/group (sql/call "GROUPING SETS"
                                                '(:unit :subunit :police :kind :subkind)
                                                '(:unit :subunit :police :kind)
                                                '(:unit :subunit :kind :subkind)
                                                '(:unit :subunit :kind)
                                                '(:unit :kind :subkind)
                                                '(:unit :kind)
                                                '(:kind :subkind)
                                                '(:kind)))
                          (sqlh/order-by [:items/unit :nulls-first]
                                         [:items/subunit :nulls-first]
                                         [:items/police :nulls-first]
                                         [:item-list/kind :nulls-first]
                                         [:item-list/subkind :nulls-first]))]
          (->> (db/honey! db sql-map {})
               not-empty
               (map (fn [m]
                      (if (nil? (:items/unit m))
                        (assoc m :items/unit "全局")
                        m))))))
       ([period]
        [malli-period => any?]
        (items-period-stats @db/sys-db period)))

