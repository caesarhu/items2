(ns items2.json-test
  (:require
    [clojure.test :refer :all]
    [items2.helpers :refer :all]
    [items2.json :refer :all]
    [java-time :as jt]
    [malli.core :as m]))


(comment
  (use-fixtures
    :once
    instrument-specs))


(def json-file "dev/resources/data/2020-11-24-11-18-20.898-DataStore.json")


(def json-test-data
  {:危安物品檔/所有項目數量 '({:all-list/item "石頭", :all-list/quantity 1}
                   {:all-list/item "高壓罐", :all-list/quantity 1}),
   :items/file-time (jt/local-date-time "2020-11-24T11:20:32") ,
   :items/ip "0.0.0.0",
   :items/passenger-sign "2020-11-24-11-18-20.898-passengerSign.jpg",
   :items/unit "安檢大隊",
   :危安物品檔/項目人數 [{:item-people/kind "危險物品",
                 :item-people/piece 1,
                 :item-people/people 0}
                {:item-people/kind "危安物品",
                 :item-people/piece 1,
                 :item-people/people 0}
                {:item-people/kind "管制藥品",
                 :item-people/piece 0,
                 :item-people/people 0}
                {:item-people/kind "槍砲彈藥(刀)械",
                 :item-people/piece 0,
                 :item-people/people 0}
                {:item-people/kind "豬肉(含肉製品)",
                 :item-people/piece 0,
                 :item-people/people 0}
                {:item-people/kind "野保法",
                 :item-people/piece 0,
                 :item-people/people 0}],
   :items/check-sign "2020-11-24-11-18-20.898-censorSign.jpg",
   :危安物品檔/日期 "2020-11-24",
   :items/process "移送相關單位",
   :items/file "2020-11-24-11-18-20.898-DataStore.json",
   :items/check-time (jt/local-date-time "2020-11-24T11:16:42.686"),
   :items/check-line "國際線",
   :items/passenger-id "A123456780",
   :危安物品檔/勤務單位 {:單位 "安檢大隊", :子單位 "第一隊"},
   :items/flight "GGG",
   :items/carry "託運",
   :危安物品檔/項目清單 [{:item-list/kind "危險物品",
                 :item-list/subkind "第二類危險氣體",
                 :item-list/object "高壓罐"}
                {:item-list/kind "危安物品",
                 :item-list/subkind "石頭",
                 :item-list/object nil}
                {:item-list/kind "危安物品",
                 :item-list/subkind "其他類備註",
                 :item-list/object nil}],
   :items/subunit "第一隊",
   :items/police "大強子",
   :items/memo "",
   :危安物品檔/時間 "11:16:42.686"})


(deftest json-parser-test
  (testing "test items2.json.json-parser"
    (is (= json-test-data (json-parser json-file)))
    (is (= true (m/validate item-schema (json-parser json-file))))))
