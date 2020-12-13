(ns items2.utils
  (:require [aave.core :refer [>defn >defn-]]
            [clojure.core :as c]
            [items2.config :as config]
            [com.rpl.specter :as sp]
            [hodur-translate.core :as hodur]
            [medley.core :as medley]
            [jsonista.core :as json]
            [java-time :as jt]
            [malli.util :as mu]
            [clojure.string :as string]))

(>defn parse-json
  [in]
  [any? => map?]
  (json/read-value in (json/object-mapper {:decode-key-fn true})))

(>defn trim-space
  [s]
  [:string => :string]
  (string/replace s #"\s+" ""))

(>defn mata-translate
  [k]
  [:keyword => :keyword]
  (hodur/dict-translate (config/meta-dict) k))

(>defn json-translate
  [k]
  [:keyword => :keyword]
  (hodur/dict-translate (config/json-dict) k))

(>defn bug-unit-translate
  [k]
  [any? => any?]
  (hodur/dict-translate (config/bug-unit-dict) k))

(>defn translate-map
  [m dict]
  [map? vector? => map?]
  (medley/map-keys #(hodur/dict-translate dict %) m))

(>defn qualify-map
  [m namespace]
  [map? [:or keyword? string? symbol?] => map?]
  (medley/map-keys #(keyword (name namespace) (name %)) m))

(>defn unqualify-map
  [m]
  [map? => map?]
  (medley/map-keys #(keyword (name %)) m))

(defn qualify-keys
  "Makes map keys qualified."
  ([?schema namespace]
   (qualify-keys ?schema namespace nil nil))
  ([?schema namespace ?keys]
   (let [[keys options] (if (map? ?keys) [nil ?keys] [?keys nil])]
     (qualify-keys ?schema namespace keys options)))
  ([?schema namespace keys options]
   (let [accept (if keys (set keys) (constantly true))
         qualify (fn [x]
                   (keyword (name namespace) (name x)))
         mapper (fn [[k :as e]] (if (accept k) (c/update e 0 qualify) e))]
     (mu/transform-entries ?schema #(map mapper %) options))))

(defn unqualify-keys
  "Makes map keys qualified."
  ([?schema]
   (unqualify-keys ?schema nil nil))
  ([?schema ?keys]
   (let [[keys options] (if (map? ?keys) [nil ?keys] [?keys nil])]
     (unqualify-keys ?schema keys options)))
  ([?schema keys options]
   (let [accept (if keys (set keys) (constantly true))
         qualify (fn [x]
                   (keyword (name x)))
         mapper (fn [[k :as e]] (if (accept k) (c/update e 0 qualify) e))]
     (mu/transform-entries ?schema #(map mapper %) options))))

(defn file-time
  [file]
  (-> (.lastModified file)
      jt/instant
      jt/fixed-clock
      jt/local-date-time))