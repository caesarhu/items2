(ns items2.utils
  (:require [aave.core :refer [>defn >defn-]]
            [clojure.core :as c]
            [items2.config :as config]
            [com.rpl.specter :as sp]
            [hodur-translate.core :as hodur]
            [medley.core :as medley]
            [java-time :as jt]
            [malli.util :as mu]
            [clojure.string :as string]))

(defn ex-cause-and-msg
  [throwable]
  (str "cause: " (medley/ex-cause throwable) "; " "message: " (medley/ex-message throwable)))

(>defn trim-space
  [s]
  [:string => :string]
  (string/replace s #"\s+" ""))

(>defn mata-translate
  [k]
  [[:or :keyword :string :symbol] => :keyword]
  (hodur/dict-translate @config/meta-dict k))

(>defn json-translate
  [k]
  [[:or :keyword :string :symbol] => :keyword]
  (hodur/dict-translate @config/json-dict k))

(>defn bug-unit-translate
  [k]
  [[:or :keyword :string :symbol] => :string]
  (hodur/dict-translate @config/bug-unit-dict k))

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

(defn qualify-malli
  "Makes map keys qualified."
  ([?schema namespace]
   (qualify-malli ?schema namespace nil nil))
  ([?schema namespace ?keys]
   (let [[keys options] (if (map? ?keys) [nil ?keys] [?keys nil])]
     (qualify-malli ?schema namespace keys options)))
  ([?schema namespace keys options]
   (let [accept (if keys (set keys) (constantly true))
         qualify (fn [x]
                   (keyword (name namespace) (name x)))
         mapper (fn [[k :as e]] (if (accept k) (c/update e 0 qualify) e))]
     (mu/transform-entries ?schema #(map mapper %) options))))

(defn unqualify-malli
  "Makes map keys qualified."
  ([?schema]
   (unqualify-malli ?schema nil nil))
  ([?schema ?keys]
   (let [[keys options] (if (map? ?keys) [nil ?keys] [?keys nil])]
     (unqualify-malli ?schema keys options)))
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


(defn partial-right
  "Takes a function f and fewer than the normal arguments to f, and
 returns a fn that takes a variable number of additional args. When
 called, the returned function calls f with additional args + args."
  ([f] f)
  ([f arg1]
   (fn [& args] (apply f (concat args [arg1]))))
  ([f arg1 arg2]
   (fn [& args] (apply f (concat args [arg1 arg2]))))
  ([f arg1 arg2 arg3]
   (fn [& args] (apply f (concat args [arg1 arg2 arg3]))))
  ([f arg1 arg2 arg3 & more]
   (fn [& args] (apply f (concat args (concat [arg1 arg2 arg3] more))))))