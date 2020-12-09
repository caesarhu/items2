(ns items2.utils
  (:require [aave.core :refer [>defn >defn-]]
            [jsonista.core :as json]))

(>defn parse-json
  [s]
  [string? => map?]
  (json/read-value s (json/object-mapper {:decode-key-fn true})))