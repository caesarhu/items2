(ns user
  (:require [nrepl.server :refer [start-server stop-server]]
            [aave.core :refer [>defn >defn-] :as aave]
            [aave.config :as aave-config]))


;;; set aave inpure

(defn set-aave-opts!
  [opts]
  (aave/set-config! (merge aave-config/default opts)))

(set-aave-opts! {:aave.core/enforce-purity false
                 :aave.core/on-purity-fail (fn [])})

(defn dev
  "Load and switch to the 'dev' namespace."
  []
  (require 'dev)
  (in-ns 'dev)
  :loaded)

;;; nrepl server

;(defonce nrepl-server (start-server :port 7777))

