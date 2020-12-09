(ns items2.json
  (:require [aave.core :refer [>defn >defn-]]
            [clojure.string :as string]
            [java-time :as jt]
            [items2.utils :as utils]
            [items2.items-malli :as im]))

(defn chinese-items-malli
  []
  (utils/translate-malli im/malli-items :items))

(>defn parse-unit
  [s]
  [:string => map?]
  (let [[_ 單位 子單位] (re-find #"(^.{4})(.*)" s)]
    {:單位 (utils/trim-space 單位)
     :子單位 (-> (utils/trim-space 子單位)
              utils/bug-unit-translate)}))

(>defn transform-unit
  [m]
  [im/malli-json => map?]
  (->> (:勤務單位 m)
       parse-unit
       (merge m)))

(>defn transform-time
  [m]
  [im/malli-json => map?]
  (let [{:keys [日期 時間]} m
        time-str (str 日期 "T" 時間)]
    (assoc m :查獲時間 (jt/local-date-time time-str))))