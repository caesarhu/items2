(ns items2.migratus
  (:require
    [items2.config :as config]
    [migratus.core :as migratus]
    [redelay.core :as redelay]))

;;; database migrations

(def migratus-config
  (redelay/state (:migratus @config/config)))


(defn migrate
  []
  (migratus/migrate @migratus-config))


(defn rollback
  []
  (migratus/rollback @migratus-config))


(defn reset-db
  []
  (migratus/reset @migratus-config))
