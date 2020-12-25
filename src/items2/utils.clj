(ns items2.utils
  (:require
    [aave.core :refer [>defn >defn-]]
    [clojure.core :as c]
    [clojure.string :as string]
    [datoteka.core :as fs]
    [exoscale.ex :as ex]
    [hodur-translate.core :as hodur]
    [hodur-translate.spec.malli-schemas :refer [local-date local-date-time]]
    [items2.config :as config]
    [java-time :as jt]
    [malli.core :as m]
    [malli.error :as me]
    [malli.util :as mu]
    [medley.core :as medley]))


(defn yesterday
  []
  {:start-date (jt/minus (jt/local-date) (jt/days 1))
   :end-date (jt/minus (jt/local-date) (jt/days 1))})


(defn last-week
  []
  {:start-date (jt/minus (jt/local-date) (jt/days 7))
   :end-date (jt/minus (jt/local-date) (jt/days 1))})


(defn last-month
  []
  (let [[year lm] (jt/as (jt/minus (jt/local-date) (jt/months 1)) :year :month-of-year)
        start-date (jt/local-date year lm 1)
        end-date (-> (jt/plus start-date (jt/months 1))
                     (jt/minus (jt/days 1)))]
    {:start-date start-date
     :end-date end-date}))


(def key-str-sym?
  [:or keyword? string? symbol?])


(>defn ns-as-table
       [k]
       [any? => any?]
       (if (keyword? k)
         (if-let [ns (namespace k)]
           (keyword (str ns "." (name k)))
           k)
         k))


(>defn day-between?
       [start-date end-date day]
       [local-date local-date local-date => boolean?]
       (let [start (jt/min start-date end-date)
             end (jt/max start-date end-date)]
         (not (or (jt/before? day start)
                  (jt/after? day end)))))


(defn ex-cause-and-msg
  [throwable]
  (str "cause: " (medley/ex-cause throwable) "; " "message: " (medley/ex-message throwable)))


(defn validate-throw
  [schema value]
  (if (m/validate schema value)
    value
    (let [msg (me/humanize (m/explain schema value))]
      (throw (ex/ex-info msg
                         ::ex/incorrect
                         {:schema schema
                          :message msg})))))


(>defn trim-space
       [s]
       [:string => :string]
       (string/replace s #"\s+" ""))


(defn meta-translate
  [k]
  (hodur/dict-translate @config/meta-dict k))


(defn json-translate
  [k]
  (hodur/dict-translate @config/json-dict k))


(defn bug-unit-translate
  [k]
  (hodur/dict-translate @config/bug-unit-dict k))


(defn translate-map
  [m dict]
  (medley/map-keys #(hodur/dict-translate dict %) m))


(defn qualify-key
  [ns k]
  (keyword (name ns) (name k)))


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


(defn optional-id-schema
  [schema]
  (let [ns (some-> schema second first namespace)
        id-key (if ns
                 (keyword ns "id")
                 :id)
        id-optional (->> (mu/select-keys schema [id-key])
                         mu/optional-keys)]
    (mu/merge schema id-optional)))


(defn dissoc-schema
  [schema & s-keys]
  (reduce (fn [init k]
            (mu/dissoc init k))
          schema s-keys))


(defn dissoc-qualified-schema
  [schema ns & s-keys]
  (let [q-keys (map #(keyword (name ns) (name %)) s-keys)]
    (reduce (fn [init k]
              (mu/dissoc init k))
            schema q-keys)))


(>defn str->int
       [int-str]
       [string? => int?]
       (Long/parseLong int-str))


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


(defn json-file
  [file-path]
  (when (and (fs/exists? file-path)
             (fs/readable? file-path)
             (fs/regular-file? file-path))
    (let [j-file (fs/file file-path)]
      (when (< 0 (.length j-file))
        j-file))))


(defn json-files
  ([max-time path]
   (let [normal-path (fs/normalize path)]
     (if (and (fs/exists? normal-path)
              (fs/directory? normal-path)
              (fs/readable? normal-path))
       (let [result (->> (fs/list-dir normal-path "*.json")
                         (map json-file)
                         (filter some?))]
         (if (jt/local-date-time? max-time)
           (filter #(jt/after? (file-time %) max-time) result)
           result))
       (throw (ex/ex-info (str path ": json directory not available!")
                          ::ex/unavailable
                          {:path path})))))
  ([max-time]
   (json-files max-time @config/json-file-path))
  ([]
   (json-files nil @config/json-file-path)))
