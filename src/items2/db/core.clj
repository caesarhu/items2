(ns items2.db.core
  (:require [items2.config :as config]
            [aave.core :refer [>defn >defn-]]
            [redelay.core :as redelay]
            [clojure.spec.alpha :as s]
            [next.jdbc :as jdbc]
            [next.jdbc.specs :as spec]
            [honeysql.core :as sql]
            [honeysql.helpers :as sqlh]
            [honeysql-postgres.helpers :as psqlh]))

(let [kebab-case# (requiring-resolve 'camel-snake-kebab.core/->kebab-case)
      snake-case# (requiring-resolve 'camel-snake-kebab.core/->snake_case)]
  (def auto-opts
    "A hash map of options that will convert Clojure identifiers to
snake_case SQL entities (`:table-fn`, `:column-fn`), and will convert
SQL entities to qualified kebab-case Clojure identifiers (`:builder-fn`)."
    {:column-fn  snake-case# :table-fn     snake-case#
     :label-fn   kebab-case# :qualifier-fn kebab-case#
     :builder-fn (resolve 'next.jdbc.result-set/as-kebab-maps)
     :return-keys true})
  (def unqualified-auto-opts
    "A hash map of options that will convert Clojure identifiers to
snake_case SQL entities (`:table-fn`, `:column-fn`), and will convert
SQL entities to unqualified kebab-case Clojure identifiers (`:builder-fn`)."
    {:column-fn  snake-case# :table-fn     snake-case#
     :label-fn   kebab-case# :qualifier-fn kebab-case#
     :builder-fn (resolve 'next.jdbc.result-set/as-unqualified-kebab-maps)
     :return-keys true}))

(s/def ::db-spec
  (s/or :db-spec ::spec/db-spec
        :next-jdbc-opts #(instance? next.jdbc.default_options.DefaultOptions %)))

(def malli-db
  [:fn #(s/valid? ::db-spec %)])
(def malli-db-opts
  [:fn #(s/valid? ::spec/opts-map %)])

(def sys-db
  (redelay/state
    (let [db (jdbc/get-datasource @config/db)]
      (jdbc/with-options db auto-opts))))

(defn db-run
  [f & args]
  (apply f @sys-db args))

(>defn honey!
  ([db sql-map opts]
   [malli-db map? malli-db-opts => any?]
   (jdbc/execute! db (sql/format sql-map) opts))
  ([sql-map opts]
   [map? malli-db-opts => any?]
   (honey! @sys-db sql-map opts))
  ([sql-map]
   [map? => any?]
   (honey! @sys-db sql-map {})))

(comment
  (s/fdef honey!
    :args (s/cat :db (s/? ::db-spec)
                 :sql-map map?
                 :opts (s/? ::spec/opts-map))))

(>defn honey-one!
  ([db sql-map opts]
   [malli-db map? malli-db-opts => any?]
   (jdbc/execute-one! db (sql/format sql-map) opts))
  ([sql-map opts]
   [map? malli-db-opts => any?]
   (honey-one! @sys-db sql-map opts))
  ([sql-map]
   [map? => any?]
   (honey-one! @sys-db sql-map {})))

(comment
  (s/fdef honey-one!
    :args (s/cat :db (s/? ::db-spec)
                 :sql-map map?
                 :opts (s/? ::spec/opts-map))))

(defn upsert-one
  [table row & conflicts]
  (let [base (sql/build :insert-into table
                        :values [row])]
    (psqlh/upsert base (apply psqlh/do-update-set
                              (apply psqlh/on-conflict {} conflicts)
                              (keys row)))))

(s/fdef upsert-one
  :args (s/cat :table keyword?
               :row map?
               :conflicts (s/+ keyword?)))