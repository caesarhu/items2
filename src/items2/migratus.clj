(ns items2.migratus
  (:require [items2.config :as config]
            [redelay.core :as redelay]
            [migratus.core :as migratus]))

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