(ns items2.db.items-test
  (:require [clojure.test :as test]
            [items2.db.items :as items]
            [java-time :as jt]
            [items2.utils :refer [file-time qualify-map]]
            [items2.test-utils :refer [instrument-specs json-file]]))

(test/use-fixtures
  :once
  instrument-specs)

(def json-full-items
  (qualify-map {:file-time (file-time json-file),
                :ip "0.0.0.0",
                :passenger-sign "2020-11-24-11-27-18.986-passengerSign.jpg",
                :unit "刑警大隊",
                :trader-sign "2020-11-24-11-27-18.986-substituteCadreSign.jpg",
                :check-sign "2020-11-24-11-27-18.986-censorSign.jpg",
                :file "full.json",
                :process "自願放棄",
                :check-time (jt/local-date-time "2020-11-24T11:20:32.302"),
                :id 48295,
                :check-line "國際線",
                :passenger-id "A123456789",
                :flight "AAA",
                :carry "手提",
                :subunit "偵一隊",
                :police "大強子",
                :memo "這是全部備註"}
               :items))

(test/deftest import-item-file!-test
  (test/testing "test import-item-file!"
    (test/is (= json-full-items (items/import-item-file! json-file)))))