(ns items2.test-utils
  (:require [clojure.spec.alpha :as s]
            [orchestra.spec.test :as stest]
            [expound.alpha :as expound]
            [items2.db.core :as db]
            [next.jdbc :as jdbc]
            [clojure.string :as string]))

(defn instrument-specs
  [f]
  (set! s/*explain-out* expound/printer)
  (stest/instrument)
  (f))

(def json-file "dev/resources/data/full.json")

(defn clear-db
  ([db]
   (let [tables ["all_list" "item_list" "item_people" "items"]
         sql-tables (string/join ", " tables)]
     (jdbc/execute! db [(str "truncate table " sql-tables " RESTART IDENTITY CASCADE")])))
  ([]
   (clear-db @db/sys-db)))