(ns items2.config
  (:require
    [aero.core :as aero]
    [clojure.java.io :as io]
    [hodur-translate.core :as hodur]
    [redelay.core :as redelay]
    [taoensso.timbre :as timbre]
    [taoensso.timbre.appenders.3rd-party.rolling :as rolling]
    [taoensso.timbre.appenders.3rd-party.rotor :as rotor]))


(defn read-edn-config
  ([profile]
   (aero/read-config (io/resource "items2/config.edn") {:profile profile}))
  ([]
   (read-edn-config :dev)))


(def config
  (redelay/state
    :start (read-edn-config)))

;;; timbre

(defn taipei-zone
  []
  (java.util.TimeZone/getTimeZone "Asia/Taipei"))


(defn rolling-appender
  [opts]
  (rolling/rolling-appender opts))


(defn rotor-appender
  [opts]
  (rotor/rotor-appender opts))


(defn set-timbre-config!
  [m]
  (timbre/set-config! timbre/default-config)
  (timbre/merge-config! m))

;;; 系統參數

;;; testing redelay

(def meta-db
  (redelay/state
    :start (-> (:schema-path (read-edn-config))
               hodur/read-schema
               hodur/init-db)))


(def meta-dict
  (redelay/state
    :start (hodur/dict-bimap @meta-db)))


(def json-dict
  (redelay/state
    :start (vector (:json-transform @config) {})))


(def bug-unit-dict
  (redelay/state
    :start [(:bug-unit @config) {}]))


(def json-file-path
  (redelay/state
    (:json-file-path @config)))
