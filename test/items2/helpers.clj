(ns items2.helpers
  (:require
    [clojure.java.io :as io]
    [clojure.spec.alpha :as s]
    [expound.alpha :as expound]
    [orchestra.spec.test :as stest]))

;;; fixtures

(defn instrument-specs
  [f]
  (set! s/*explain-out* expound/printer)
  (stest/instrument)
  (f))
