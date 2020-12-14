(ns dev
  (:require [fipp.edn :refer [pprint]]
            [clojure.spec.alpha :as s]
            [clojure.java.io :as io]
            [expound.alpha :as expound]
            [orchestra.spec.test :as stest]
            [items2.config :as config]
            [items2.migratus :as migratus]
            [juxt.clip.repl :refer [start stop set-init! reset system]]
            [hodur-translate.core :as hodur]
            [items2.utils :as utils]
            [clojure.string :as string]
            [next.jdbc :as jdbc]
            [items2.db.core :as db]
            [items2.db.items :as items]
            [next.jdbc.specs :as spec]
            [malli.core :as m]
            [malli.transform :as mt]
            [honeysql.core :as sql]
            [honeysql.helpers :as sqlh]
            [items2.json :as j]
            [items2.db.items-child :as child]))

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

(defn slurp-json
  [path]
  (let [file (io/resource (str "data/" path))]
    (slurp file)))

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

(def json-file "dev/resources/data/2020-11-24-11-18-20.898-DataStore.json")