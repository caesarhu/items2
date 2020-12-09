(ns items2.utils
  (:require [aave.core :refer [>defn >defn-]]
            [items2.config :as config]
            [com.rpl.specter :as sp]
            [hodur-translate.core :as hodur]
            [jsonista.core :as json]
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

(>defn translate-malli
  [malli namespace]
  [vector? any? => vector?]
  (sp/transform [sp/ALL vector? sp/FIRST] #(->> (mata-translate (keyword (name namespace) (name %)))
                                                name
                                                keyword) malli))