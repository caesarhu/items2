(ns items2.csv
  (:require [aave.core :refer [>defn >defn-]]
            [clj-bom.core :as bom]
            [clojure.data.csv :as csv]
            [java-time :as jt]
            [items2.utils :as utils]))

(def time-format "YYYY/MM/dd HH:mm:ss")
(def date-format "YYYY/MM/dd")

(defn date-time-transform
  [field]
  (cond
    (jt/local-date-time? field) (jt/format time-format field)
    (jt/local-date? field) (jt/format date-format field)
    :else field))

(>defn ->csv-row
  [m kv]
  [map? [:vector keyword?] => seq?]
  (map (fn [m k]
         (->> (get m k)
              date-time-transform))
       (repeat m)
       kv))

(>defn values->seq
  ([map-v kv title?]
   [[:sequential map?] [:vector keyword?] boolean? => seq?]
   (let [title (map (fn [k]
                      (->> k utils/meta-translate name))
                    kv)
         csv-vec (map #(->csv-row % kv) map-v)]
     (if title?
       (cons title csv-vec)
       csv-vec)))
  ([map-v kv]
   [[:sequential map?] [:vector keyword?] => seq?]
   (values->seq map-v kv true)))

(>defn spit-values-csv
  ([path values kv title?]
   [any? [:sequential map?] [:vector keyword?] boolean? => any?]
   (let [csv (values->seq values kv title?)]
     (with-open [writer (bom/bom-writer "UTF-8" path)]
       (csv/write-csv writer csv))))
  ([path values kv]
   [any? [:sequential map?] [:vector keyword?] => any?]
   (spit-values-csv path values kv true)))