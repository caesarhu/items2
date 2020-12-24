(ns items2.db.items-csv
  (:require
    [aave.core :refer [>defn >defn-]]
    [clojure.string :as string]
    [datoteka.core :as fs]
    [items2.config :as config]
    [items2.csv :as csv]
    [items2.db.core :as db]
    [items2.db.mail-list :as mail]
    [java-time :as jt]))


(>defn make-csv-name
       [period mail-group report]
       [map? any? any? => string?]
       (let [{:keys [start-date end-date]} period
             unit-name (key mail-group)
             base-name (:name (val report))]
         (-> (string/join "-" [base-name unit-name (jt/format "YYYY-MM-dd" start-date)
                               "to" (jt/format "YYYY-MM-dd" end-date)])
             (str ".csv"))))


(>defn csv-path
       ([csv-dir csv-name]
        [string? string? => string?]
        (str (fs/normalize csv-dir) "/" csv-name))
       ([csv-name]
        [string? => string?]
        (csv-path (:csv-path @config/config) csv-name)))


(>defn filter-units
       ([values unit subunit]
        [coll? [:or string? nil?] [:or string? nil?] => any?]
        (let [units? (fn [m]
                       (if subunit
                         (= [unit subunit] ((juxt :items/unit :items/subunit) m))
                         (if unit
                           (= unit (:items/unit m))
                           true)))]
          (filter units? values)))
       ([values unit]
        [coll? [:or string? nil?] => any?]
        (filter-units values unit nil))
       ([values]
        [coll? => any?]
        values))


(>defn make-unit-csv
       ([csv-dir values period mail-group report]
        [any? coll? map? any? any? => any?]
        (let [csv-name (make-csv-name period mail-group report)
              unit-name (key mail-group)
              [unit subunit] (-> mail-group val first ((juxt :unit :subunit)))
              csv-path (csv-path csv-dir csv-name)
              csv-fields (:fields (val report))
              data (if (= "全局" unit-name)
                     values
                     (filter-units values unit subunit))]
          (csv/spit-values-csv csv-path data csv-fields)))
       ([values period mail-group report]
        [coll? map? any? any? => any?]
        (make-unit-csv (:csv-path @config/config) values period mail-group report)))


(>defn make-report-csv
       ([db period report csv-dir]
        [db/malli-db map? any? any? => any?]
        (fs/create-dir (fs/normalize csv-dir))
        (let [report-name (name (key report))
              report-fn (symbol (str "items2.db.stats/items-period-" report-name))
              values (@(resolve report-fn) db period)
              mail-list (mail/get-mail-list db)]
          (map #(make-unit-csv csv-dir values period % report) mail-list)))
       ([period report csv-dir]
        [map? any? any? => any?]
        (make-report-csv @db/sys-db period report csv-dir))
       ([period report]
        [map? any? => any?]
        (make-report-csv @db/sys-db period report (:csv-path @config/config))))


(defn spit-reports-csv
  ([db period reports csv-dir]
   (mapv #(make-report-csv db period % csv-dir) reports))
  ([db period reports]
   (spit-reports-csv db period reports (:csv-path @config/config)))
  ([db period]
   (spit-reports-csv db period (:report @config/config)))
  ([period]
   (spit-reports-csv @db/sys-db period)))

