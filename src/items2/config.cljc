(ns items2.config
  (:require [aero.core :as aero]
            [clojure.java.io :as io]
            [migratus.core :as migratus]
            [taoensso.timbre :as timbre]
            [taoensso.timbre.appenders.3rd-party.rolling :as rolling]))

(defn config
  ([profile]
   (aero/read-config (io/resource "config.edn") {:profile profile}))
  ([]
   (config :dev)))

;;; database migrations

(defn migratus-config
  ([tag]
   (:migratus (config tag)))
  ([]
   (migratus-config :dev)))

(defn migrate
  ([tag]
   (migratus/migrate (migratus-config tag)))
  ([]
   (migrate :dev)))

(defn rollback
  ([tag]
   (migratus/rollback (migratus-config tag)))
  ([]
   (rollback :dev)))

;;; timbre

(defn taipei-zone
  []
  (java.util.TimeZone/getTimeZone "Asia/Taipei"))

(defn rolling-appender
  [opts]
  (rolling/rolling-appender opts))

(defn set-timbre-config!
  [m]
  (timbre/set-config! timbre/default-config)
  (timbre/merge-config! m))