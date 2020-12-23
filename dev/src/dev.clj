(ns dev
  (:require
    [clojure.java.io :as io]
    [clojure.spec.alpha :as s]
    [clojure.string :as string]
    [datoteka.core :as fs]
    [expound.alpha :as expound]
    [fipp.edn :refer [pprint]]
    [hodur-translate.core :as hodur]
    [honeysql.core :as sql]
    [honeysql.helpers :as sqlh]
    [items2.config :as config]
    [items2.db.core :as db]
    [items2.db.items :as items]
    [items2.db.items-csv :as csv]
    [items2.db.mail :as mail]
    [items2.migratus :as migratus]
    [items2.utils :as utils]
    [java-time :as jt]
    [juxt.clip.repl :refer [start stop set-init! reset system]]
    [malli.core :as m]
    [malli.provider :as mp]
    [malli.transform :as mt]
    [malli.util :as mu]
    [orchestra.spec.test :as stest]))

;(set-init! (fn [] (config/read-edn-config :dev)))
(set-init! (fn [] @config/config))


(comment
  (start)
  (reset)
  (stop)
  system)

;;; code start

(defn meta-db
  []
  (-> (:schema-path (config/read-edn-config))
      hodur/read-schema
      hodur/init-db))


(defn spit-sql
  []
  (-> (str "resources/" (:migration-dir (config/read-edn-config)))
      (hodur/spit-sql (meta-db))))


(defn spit-malli
  [path]
  (hodur/spit-malli-schema path (meta-db) true))

;;; expound and Orchestra

(defn unstrument
  []
  (stest/unstrument))


(defn instrument
  []
  (set! s/*explain-out* expound/printer)
  (stest/instrument))


(instrument)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def period
  {:start-date (jt/local-date 2020 12 21)
   :end-date (jt/local-date 2020 12 21)})


(def items-csv
  ["csv/明細-保安大隊-2020-12-22-to-2020-12-22.csv"
   "csv/明細-全局-2020-12-22-to-2020-12-22.csv"
   "csv/統計-保安大隊-2020-12-22-to-2020-12-22.csv"
   "csv/統計-全局-2020-12-22-to-2020-12-22.csv"])
