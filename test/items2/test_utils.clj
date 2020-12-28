(ns items2.test-utils
  (:require [clojure.spec.alpha :as s]
            [orchestra.spec.test :as stest]
            [expound.alpha :as expound]))

(defn instrument-specs
  [f]
  (set! s/*explain-out* expound/printer)
  (stest/instrument)
  (f))

(def json-file "dev/resources/data/full.json")