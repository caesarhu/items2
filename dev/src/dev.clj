(ns dev
  (:require [fipp.edn :refer [pprint]]
            [clojure.spec.alpha :as spec.alpha]
            [clojure.java.io :as io]
            [expound.alpha :as expound]
            [orchestra.spec.test :as stest]
            [items2.config :as config]
            [juxt.clip.repl :refer [start stop set-init! reset system]]
            [hodur-translate.core :as hodur]
            [items2.utils :as utils]
            [java-time :as jt]
            [medley.core :as medley]
            [jsonista.core :as json]
            [malli.core :as m]
            [malli.transform :as mt]
            [malli.provider :as mp]
            [items2.items-malli :as im]
            [com.rpl.specter :as sp]
            [exoscale.ex :as ex]
            [items2.json :as j]
            [items2.transform :as t]
            [datoteka.core :as fs]
            [malli.util :as mu]
            [malli.error :as me]
            [kaocha.repl :as k]))

(set-init! (fn [] (config/config :dev)))

(comment
  (start)
  (reset)
  (stop)
  system)

;;; code start

(defn meta-db
  []
  (-> (:schema-path (config/config))
      hodur/read-schema
      hodur/init-db))

(defn spit-sql
  []
  (-> (str "resources/" (:migration-dir (config/config)))
      (hodur/spit-sql (meta-db))))

(defn spit-malli
  [path]
  (hodur/spit-malli-schema path (meta-db)))

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
  (set! spec.alpha/*explain-out* expound/printer)
  (stest/instrument))

(instrument)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;