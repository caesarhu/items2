(ns dev
  (:refer-clojure :exclude [test])
  (:require
    [aave.config :as aave-config]
    [aave.core :refer [>defn >defn-] :as aave]
    [clojure.java.io :as io]
    [clojure.spec.alpha :as s]
    [expound.alpha :as expound]
    [fipp.edn :refer [pprint]]
    [hodur-translate.core :as hodur]
    [items2.config :as config]
    [clojure.tools.gitlibs :as gl]
    [items2.migratus :as migratus]
    [java-time :as jt]
    [juxt.clip.repl :refer [start stop set-init! reset system]]
    [kaocha.repl :as k]
    [orchestra.spec.test :as stest]))

;;; set aave inpure

(defn set-aave-opts!
  [opts]
  (aave/set-config! (merge aave-config/default opts)))


(set-aave-opts! {:aave.core/enforce-purity false
                 :aave.core/on-purity-fail (fn [])})

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


(defn test
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

