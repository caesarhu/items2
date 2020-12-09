(ns items2.utils
  (:require [aave.core :refer [>defn >defn-]]
            [jsonista.core :as json]))

(>defn parse-json
  [in]
  [any? => map?]
  (json/read-value in (json/object-mapper {:decode-key-fn true})))