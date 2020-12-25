(ns dev
  (:require
    [clojure.java.io :as io]
    [clojure.spec.alpha :as s]
    [expound.alpha :as expound]
    [fipp.edn :refer [pprint]]
    [hodur-translate.core :as hodur]
    [items2.config :as config]
    [items2.migratus :as migratus]
    [java-time :as jt]
    [juxt.clip.repl :refer [start stop set-init! reset system]]
    [kaocha.repl :as k]
    [orchestra.spec.test :as stest]
    [items2.db.items :as items]
    [items2.json :as json]
    [datoteka.core :as fs]
    [clojure.tools.gitlibs :as gl]))

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


(defn dev-test
  []
  (k/run :unit))

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

