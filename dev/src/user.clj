(ns user
  (:require [nrepl.server :refer [start-server stop-server]]))


(defn dev
  "Load and switch to the 'dev' namespace."
  []
  (require 'dev)
  (in-ns 'dev)
  :loaded)

;;; nrepl server

;(defonce nrepl-server (start-server :port 7777))

