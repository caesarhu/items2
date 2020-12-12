(ns items2.utils
  (:require [aave.core :refer [>defn >defn-]]
            [items2.config :as config]
            [com.rpl.specter :as sp]
            [hodur-translate.core :as hodur]
            [medley.core :as medley]
            [jsonista.core :as json]
            [java-time :as jt]
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

(>defn quality-malli
  [malli namespace]
  [vector? any? => vector?]
  (sp/transform [sp/ALL vector? sp/FIRST] #(keyword (name namespace) (name %)) malli))

(defn file-time
  [file]
  (-> (.lastModified file)
      jt/instant
      jt/fixed-clock
      jt/local-date-time))