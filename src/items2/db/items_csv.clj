(ns items2.db.items-csv
  (:require
    [aave.core :refer [>defn >defn-]]
    [clojure.string :as string]
    [datoteka.core :as fs]
    [items2.config :as config]
    [items2.csv :as csv]
    [items2.db.core :as db]
    [items2.db.mail-list :as mail]
    [java-time :as jt]
    [items2.db.stats :as stats]))


(defn make-csv-name
  [period mail-group report]
  (let [{:keys [start-date end-date]} period
        unit-name (key mail-group)
        base-name (:name (val report))]
    (-> (string/join "-" [base-name unit-name (jt/format :iso-local-date start-date)
                          "to" (jt/format :iso-local-date end-date)])
        (str ".csv"))))


(defn csv-path
  ([csv-dir csv-name]
   (str (fs/normalize csv-dir) "/" csv-name))
  ([csv-name]
   (csv-path (:csv-path @config/config) csv-name)))


(defn filter-units
  ([values unit subunit]
   (let [units? (fn [m]
                  (if subunit
                    (= [unit subunit] ((juxt :items/unit :items/subunit) m))
                    (if unit
                      (= unit (:items/unit m))
                      true)))]
     (filter units? values)))
  ([values unit]
   (filter-units values unit nil))
  ([values]
   values))


(defn make-unit-csv
  ([csv-dir values period mail-group report]
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
   (make-unit-csv (:csv-path @config/config) values period mail-group report)))


(defn make-report-csv
  ([db period report csv-dir]
   (fs/create-dir (fs/normalize csv-dir))
   (let [report-name (name (key report))
         values (case report-name
                  "detail" (stats/items-period-detail db period)
                  "stats" (stats/items-period-stats  db period))
         mail-list (mail/get-mail-list db)]
     (map #(make-unit-csv csv-dir values period % report) mail-list)))
  ([period report csv-dir]
   (make-report-csv @db/sys-db period report csv-dir))
  ([period report]
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

