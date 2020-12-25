(ns user
  (:require
    [aave.config :as aave-config]
    [aave.core :refer [>defn >defn-] :as aave]))

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


