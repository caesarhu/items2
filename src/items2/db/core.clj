(ns items2.db.core
  (:require [next.jdbc.date-time :refer [read-as-local]]
            [items2.config :as config]
            [aave.core :refer [>defn >defn-] :as aave]
            [hikari-cp.core :as hikari]
            [redelay.core :as redelay]
            [clojure.spec.alpha :as s]
            [next.jdbc :as jdbc]
            [next.jdbc.specs :as spec]
            [honeysql.core :as sql]
            [honeysql.helpers :as sqlh]
            [honeysql-postgres.helpers :as psqlh]))

;;; java-time convert

(read-as-local)

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
        :next-jdbc-opts #(instance? next.jdbc.default_options.DefaultOptions %)
        :pg-connection #(instance? org.postgresql.jdbc.PgConnection %)
        :hikari-cp #(instance? com.zaxxer.hikari.pool.HikariProxyConnection %)))

(def malli-db
  [:fn #(s/valid? ::db-spec %)])
(def malli-db-opts
  [:fn #(s/valid? ::spec/opts-map %)])

;;; database datasource

(def sys-db
  (redelay/state :start
                 (hikari/make-datasource (:hikari-cp @config/config))

                 :stop
                 (hikari/close-datasource this)))

(>defn honey-format
  [map-or-seq-or-vector]
  [[:or :map :sequential :vector] => vector?]
  (if (map? map-or-seq-or-vector)
    (sql/format map-or-seq-or-vector :namespace-as-table? true)
    (apply sql/format map-or-seq-or-vector)))

(>defn honey!
  ([db sql-map opts]
   [malli-db [:or :map :sequential :vector] malli-db-opts => any?]
   (jdbc/execute! db (honey-format sql-map) (merge auto-opts opts)))
  ([sql-map opts]
   [[:or :map :sequential :vector] malli-db-opts => any?]
   (honey! @sys-db sql-map opts))
  ([sql-map]
   [[:or :map :sequential :vector] => any?]
   (honey! @sys-db sql-map {})))

(>defn honey-one!
  ([db sql-map opts]
   [malli-db [:or :map :sequential :vector] malli-db-opts => any?]
   (jdbc/execute-one! db (honey-format sql-map) (merge auto-opts opts)))
  ([sql-map opts]
   [[:or :map :sequential :vector] malli-db-opts => any?]
   (honey-one! @sys-db sql-map opts))
  ([sql-map]
   [[:or :map :sequential :vector] => any?]
   (honey-one! @sys-db sql-map {})))

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
