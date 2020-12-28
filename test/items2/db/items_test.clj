(ns items2.db.items-test
  (:require [clojure.test :as test]
            [items2.db.items :as items]
            [java-time :as jt]
            [honeysql.core :as sql]
            [honeysql.helpers :as sqlh]
            [items2.db.core :as db]
            [items2.utils :refer [file-time qualify-map]]
            [items2.test-utils :refer [instrument-specs json-file clear-db]]))

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
                :id 1,
                :check-line "國際線",
                :passenger-id "A123456789",
                :flight "AAA",
                :carry "手提",
                :subunit "偵一隊",
                :police "大強子",
                :memo "這是全部備註"}
               :items))

(def all-list-full
  [{:all-list/id 1,
    :all-list/item "高爾夫球桿",
    :all-list/items-id 1,
    :all-list/quantity 1}
   {:all-list/id 2,
    :all-list/item "電池酸液",
    :all-list/items-id 1,
    :all-list/quantity 1}
   {:all-list/id 3,
    :all-list/item "棍棒、工具機及農具類",
    :all-list/items-id 1,
    :all-list/quantity 1}
   {:all-list/id 4,
    :all-list/item "白磷、黃磷",
    :all-list/items-id 1,
    :all-list/quantity 1}
   {:all-list/id 5,
    :all-list/item "有機過氧化物B型態",
    :all-list/items-id 1,
    :all-list/quantity 1}
   {:all-list/id 6,
    :all-list/item "尖銳物品類",
    :all-list/items-id 1,
    :all-list/quantity 1}
   {:all-list/id 7,
    :all-list/item "小白板",
    :all-list/items-id 1,
    :all-list/quantity 1}
   {:all-list/id 8,
    :all-list/item "煤油",
    :all-list/items-id 1,
    :all-list/quantity 1}
   {:all-list/id 9,
    :all-list/item "乙醚",
    :all-list/items-id 1,
    :all-list/quantity 1}
   {:all-list/id 10,
    :all-list/item "安非他命",
    :all-list/items-id 1,
    :all-list/quantity 1}
   {:all-list/id 11,
    :all-list/item "酒",
    :all-list/items-id 1,
    :all-list/quantity 1}
   {:all-list/id 12,
    :all-list/item "FM2",
    :all-list/items-id 1,
    :all-list/quantity 1}
   {:all-list/id 13,
    :all-list/item "石頭",
    :all-list/items-id 1,
    :all-list/quantity 1}
   {:all-list/id 14,
    :all-list/item "磁性物質",
    :all-list/items-id 1,
    :all-list/quantity 1}
   {:all-list/id 15,
    :all-list/item "鐳",
    :all-list/items-id 1,
    :all-list/quantity 1}
   {:all-list/id 16,
    :all-list/item "砒霜、農藥",
    :all-list/items-id 1,
    :all-list/quantity 1}
   {:all-list/id 17,
    :all-list/item "汞",
    :all-list/items-id 1,
    :all-list/quantity 1}
   {:all-list/id 18,
    :all-list/item "古柯鹼",
    :all-list/items-id 1,
    :all-list/quantity 1}
   {:all-list/id 19,
    :all-list/item "仙女棒",
    :all-list/items-id 1,
    :all-list/quantity 1}
   {:all-list/id 20,
    :all-list/item "鴉片",
    :all-list/items-id 1,
    :all-list/quantity 1}
   {:all-list/id 21,
    :all-list/item "制式長槍",
    :all-list/items-id 1,
    :all-list/quantity 1}
   {:all-list/id 22,
    :all-list/item "大麻",
    :all-list/items-id 1,
    :all-list/quantity 1}
   {:all-list/id 23,
    :all-list/item "砷",
    :all-list/items-id 1,
    :all-list/quantity 1}
   {:all-list/id 24,
    :all-list/item "甲苯",
    :all-list/items-id 1,
    :all-list/quantity 1}
   {:all-list/id 25,
    :all-list/item "石灰",
    :all-list/items-id 1,
    :all-list/quantity 1}
   {:all-list/id 26,
    :all-list/item "MDMA",
    :all-list/items-id 1,
    :all-list/quantity 1}
   {:all-list/id 27,
    :all-list/item "其他",
    :all-list/items-id 1,
    :all-list/quantity 1}
   {:all-list/id 28,
    :all-list/item "Alprazolam(蝴蝶片)",
    :all-list/items-id 1,
    :all-list/quantity 1}
   {:all-list/id 29,
    :all-list/item "滑板",
    :all-list/items-id 1,
    :all-list/quantity 1}
   {:all-list/id 30,
    :all-list/item "鎂粉",
    :all-list/items-id 1,
    :all-list/quantity 1}
   {:all-list/id 31,
    :all-list/item "管制先驅原料",
    :all-list/items-id 1,
    :all-list/quantity 1}
   {:all-list/id 32,
    :all-list/item "Diazepam(安定、煩寧)",
    :all-list/items-id 1,
    :all-list/quantity 1}
   {:all-list/id 33,
    :all-list/item "Lorazepam(一粒眠)",
    :all-list/items-id 1,
    :all-list/quantity 1}
   {:all-list/id 34,
    :all-list/item "球棒",
    :all-list/items-id 1,
    :all-list/quantity 1}
   {:all-list/id 35,
    :all-list/item "海洛因",
    :all-list/items-id 1,
    :all-list/quantity 1}
   {:all-list/id 36,
    :all-list/item "醫療廢棄物",
    :all-list/items-id 1,
    :all-list/quantity 1}
   {:all-list/id 37,
    :all-list/item "刀剪類",
    :all-list/items-id 1,
    :all-list/quantity 1}
   {:all-list/id 38,
    :all-list/item "高壓罐",
    :all-list/items-id 1,
    :all-list/quantity 100}
   {:all-list/id 39,
    :all-list/item "漂白水",
    :all-list/items-id 1,
    :all-list/quantity 1}
   {:all-list/id 40,
    :all-list/item "液狀、膠狀及噴霧物品類",
    :all-list/items-id 1,
    :all-list/quantity 1}
   {:all-list/id 41,
    :all-list/item "汽油",
    :all-list/items-id 1,
    :all-list/quantity 1}
   {:all-list/id 42,
    :all-list/item "制式短槍",
    :all-list/items-id 1,
    :all-list/quantity 1}
   {:all-list/id 43,
    :all-list/item "LSD",
    :all-list/items-id 1,
    :all-list/quantity 1}
   {:all-list/id 44,
    :all-list/item "玩具槍類",
    :all-list/items-id 1,
    :all-list/quantity 1}
   {:all-list/id 45,
    :all-list/item "空氣槍",
    :all-list/items-id 1,
    :all-list/quantity 1}
   {:all-list/id 46,
    :all-list/item "土、改造槍",
    :all-list/items-id 1,
    :all-list/quantity 1}
   {:all-list/id 47,
    :all-list/item "爆竹",
    :all-list/items-id 1,
    :all-list/quantity 1}
   {:all-list/id 48,
    :all-list/item "丁基原啡因",
    :all-list/items-id 1,
    :all-list/quantity 1}
   {:all-list/id 49,
    :all-list/item "硫酸(含酸超過51％)",
    :all-list/items-id 1,
    :all-list/quantity 1}
   {:all-list/id 50,
    :all-list/item "嗎啡",
    :all-list/items-id 1,
    :all-list/quantity 1}
   {:all-list/id 51,
    :all-list/item "K他命",
    :all-list/items-id 1,
    :all-list/quantity 1}
   {:all-list/id 52,
    :all-list/item "漆類",
    :all-list/items-id 1,
    :all-list/quantity 1}
   {:all-list/id 53,
    :all-list/item "打火機",
    :all-list/items-id 1,
    :all-list/quantity 10}
   {:all-list/id 54,
    :all-list/item "鈾",
    :all-list/items-id 1,
    :all-list/quantity 1}
   {:all-list/id 55,
    :all-list/item "硝酸鈉",
    :all-list/items-id 1,
    :all-list/quantity 1}
   {:all-list/id 56,
    :all-list/item "鋰電池",
    :all-list/items-id 1,
    :all-list/quantity 1000}
   {:all-list/id 57,
    :all-list/item "鈽",
    :all-list/items-id 1,
    :all-list/quantity 1}
   {:all-list/id 58,
    :all-list/item "環境危害物質",
    :all-list/items-id 1,
    :all-list/quantity 1}
   {:all-list/id 59,
    :all-list/item "活性碳",
    :all-list/items-id 1,
    :all-list/quantity 1}
   {:all-list/id 60,
    :all-list/item "管制刀械",
    :all-list/items-id 1,
    :all-list/quantity 1}])

(def item-list-full
  [{:item-list/id 1,
    :item-list/items-id 1,
    :item-list/kind "危險物品",
    :item-list/object "爆竹",
    :item-list/subkind "第一類爆炸性物品"}
   {:item-list/id 2,
    :item-list/items-id 1,
    :item-list/kind "危險物品",
    :item-list/object "仙女棒",
    :item-list/subkind "第一類爆炸性物品"}
   {:item-list/id 3,
    :item-list/items-id 1,
    :item-list/kind "危險物品",
    :item-list/object "打火機",
    :item-list/subkind "第二類危險氣體"}
   {:item-list/id 4,
    :item-list/items-id 1,
    :item-list/kind "危險物品",
    :item-list/object "高壓罐",
    :item-list/subkind "第二類危險氣體"}
   {:item-list/id 5,
    :item-list/items-id 1,
    :item-list/kind "危險物品",
    :item-list/object "漆類",
    :item-list/subkind "第三類易燃性液體"}
   {:item-list/id 6,
    :item-list/items-id 1,
    :item-list/kind "危險物品",
    :item-list/object "汽油",
    :item-list/subkind "第三類易燃性液體"}
   {:item-list/id 7,
    :item-list/items-id 1,
    :item-list/kind "危險物品",
    :item-list/object "煤油",
    :item-list/subkind "第三類易燃性液體"}
   {:item-list/id 8,
    :item-list/items-id 1,
    :item-list/kind "危險物品",
    :item-list/object "甲苯",
    :item-list/subkind "第三類易燃性液體"}
   {:item-list/id 9,
    :item-list/items-id 1,
    :item-list/kind "危險物品",
    :item-list/object "乙醚",
    :item-list/subkind "第三類易燃性液體"}
   {:item-list/id 10,
    :item-list/items-id 1,
    :item-list/kind "危險物品",
    :item-list/object "酒",
    :item-list/subkind "第三類易燃性液體"}
   {:item-list/id 11,
    :item-list/items-id 1,
    :item-list/kind "危險物品",
    :item-list/object "活性碳",
    :item-list/subkind "第四類易燃固體、自燃物質、遇水釋放易燃氣體之物質"}
   {:item-list/id 12,
    :item-list/items-id 1,
    :item-list/kind "危險物品",
    :item-list/object "白磷、黃磷",
    :item-list/subkind "第四類易燃固體、自燃物質、遇水釋放易燃氣體之物質"}
   {:item-list/id 13,
    :item-list/items-id 1,
    :item-list/kind "危險物品",
    :item-list/object "鎂粉",
    :item-list/subkind "第四類易燃固體、自燃物質、遇水釋放易燃氣體之物質"}
   {:item-list/id 14,
    :item-list/items-id 1,
    :item-list/kind "危險物品",
    :item-list/object "硝酸鈉",
    :item-list/subkind "第五類氧化物及有機過氧化物"}
   {:item-list/id 15,
    :item-list/items-id 1,
    :item-list/kind "危險物品",
    :item-list/object "漂白水",
    :item-list/subkind "第五類氧化物及有機過氧化物"}
   {:item-list/id 16,
    :item-list/items-id 1,
    :item-list/kind "危險物品",
    :item-list/object "有機過氧化物B型態",
    :item-list/subkind "第五類氧化物及有機過氧化物"}
   {:item-list/id 17,
    :item-list/items-id 1,
    :item-list/kind "危險物品",
    :item-list/object "砷",
    :item-list/subkind "第六類毒性物質及傳染性物質"}
   {:item-list/id 18,
    :item-list/items-id 1,
    :item-list/kind "危險物品",
    :item-list/object "砒霜、農藥",
    :item-list/subkind "第六類毒性物質及傳染性物質"}
   {:item-list/id 19,
    :item-list/items-id 1,
    :item-list/kind "危險物品",
    :item-list/object "醫療廢棄物",
    :item-list/subkind "第六類毒性物質及傳染性物質"}
   {:item-list/id 20,
    :item-list/items-id 1,
    :item-list/kind "危險物品",
    :item-list/object "鈾",
    :item-list/subkind "第七類放射性物質"}
   {:item-list/id 21,
    :item-list/items-id 1,
    :item-list/kind "危險物品",
    :item-list/object "鈽",
    :item-list/subkind "第七類放射性物質"}
   {:item-list/id 22,
    :item-list/items-id 1,
    :item-list/kind "危險物品",
    :item-list/object "鐳",
    :item-list/subkind "第七類放射性物質"}
   {:item-list/id 23,
    :item-list/items-id 1,
    :item-list/kind "危險物品",
    :item-list/object "硫酸(含酸超過51％)",
    :item-list/subkind "第八類腐蝕性物質"}
   {:item-list/id 24,
    :item-list/items-id 1,
    :item-list/kind "危險物品",
    :item-list/object "電池酸液",
    :item-list/subkind "第八類腐蝕性物質"}
   {:item-list/id 25,
    :item-list/items-id 1,
    :item-list/kind "危險物品",
    :item-list/object "汞",
    :item-list/subkind "第八類腐蝕性物質"}
   {:item-list/id 26,
    :item-list/items-id 1,
    :item-list/kind "危險物品",
    :item-list/object "石灰",
    :item-list/subkind "第八類腐蝕性物質"}
   {:item-list/id 27,
    :item-list/items-id 1,
    :item-list/kind "危險物品",
    :item-list/object "鋰電池",
    :item-list/subkind "第九類其他"}
   {:item-list/id 28,
    :item-list/items-id 1,
    :item-list/kind "危險物品",
    :item-list/object "磁性物質",
    :item-list/subkind "第九類其他"}
   {:item-list/id 29,
    :item-list/items-id 1,
    :item-list/kind "危險物品",
    :item-list/object "環境危害物質",
    :item-list/subkind "第九類其他"}
   {:item-list/id 30,
    :item-list/items-id 1,
    :item-list/kind "危安物品",
    :item-list/object nil,
    :item-list/subkind "刀剪類"}
   {:item-list/id 31,
    :item-list/items-id 1,
    :item-list/kind "危安物品",
    :item-list/object nil,
    :item-list/subkind "尖銳物品類"}
   {:item-list/id 32,
    :item-list/items-id 1,
    :item-list/kind "危安物品",
    :item-list/object nil,
    :item-list/subkind "棍棒、工具機及農具類"}
   {:item-list/id 33,
    :item-list/items-id 1,
    :item-list/kind "危安物品",
    :item-list/object nil,
    :item-list/subkind "玩具槍類"}
   {:item-list/id 34,
    :item-list/items-id 1,
    :item-list/kind "危安物品",
    :item-list/object nil,
    :item-list/subkind "液狀、膠狀及噴霧物品類"}
   {:item-list/id 35,
    :item-list/items-id 1,
    :item-list/kind "危安物品",
    :item-list/object nil,
    :item-list/subkind "石頭"}
   {:item-list/id 36,
    :item-list/items-id 1,
    :item-list/kind "危安物品",
    :item-list/object "球棒",
    :item-list/subkind "運動用品類"}
   {:item-list/id 37,
    :item-list/items-id 1,
    :item-list/kind "危安物品",
    :item-list/object "高爾夫球桿",
    :item-list/subkind "運動用品類"}
   {:item-list/id 38,
    :item-list/items-id 1,
    :item-list/kind "危安物品",
    :item-list/object "滑板",
    :item-list/subkind "運動用品類"}
   {:item-list/id 39,
    :item-list/items-id 1,
    :item-list/kind "危安物品",
    :item-list/object "這是危安物品備註",
    :item-list/subkind "其他類備註"}
   {:item-list/id 40,
    :item-list/items-id 1,
    :item-list/kind "管制藥品",
    :item-list/object "海洛因",
    :item-list/subkind "第一級"}
   {:item-list/id 41,
    :item-list/items-id 1,
    :item-list/kind "管制藥品",
    :item-list/object "嗎啡",
    :item-list/subkind "第一級"}
   {:item-list/id 42,
    :item-list/items-id 1,
    :item-list/kind "管制藥品",
    :item-list/object "鴉片",
    :item-list/subkind "第一級"}
   {:item-list/id 43,
    :item-list/items-id 1,
    :item-list/kind "管制藥品",
    :item-list/object "古柯鹼",
    :item-list/subkind "第一級"}
   {:item-list/id 44,
    :item-list/items-id 1,
    :item-list/kind "管制藥品",
    :item-list/object "其他",
    :item-list/subkind "第一級"}
   {:item-list/id 45,
    :item-list/items-id 1,
    :item-list/kind "管制藥品",
    :item-list/object "安非他命",
    :item-list/subkind "第二級"}
   {:item-list/id 46,
    :item-list/items-id 1,
    :item-list/kind "管制藥品",
    :item-list/object "MDMA",
    :item-list/subkind "第二級"}
   {:item-list/id 47,
    :item-list/items-id 1,
    :item-list/kind "管制藥品",
    :item-list/object "大麻",
    :item-list/subkind "第二級"}
   {:item-list/id 48,
    :item-list/items-id 1,
    :item-list/kind "管制藥品",
    :item-list/object "LSD",
    :item-list/subkind "第二級"}
   {:item-list/id 49,
    :item-list/items-id 1,
    :item-list/kind "管制藥品",
    :item-list/object "其他",
    :item-list/subkind "第二級"}
   {:item-list/id 50,
    :item-list/items-id 1,
    :item-list/kind "管制藥品",
    :item-list/object "FM2",
    :item-list/subkind "第三級"}
   {:item-list/id 51,
    :item-list/items-id 1,
    :item-list/kind "管制藥品",
    :item-list/object "小白板",
    :item-list/subkind "第三級"}
   {:item-list/id 52,
    :item-list/items-id 1,
    :item-list/kind "管制藥品",
    :item-list/object "丁基原啡因",
    :item-list/subkind "第三級"}
   {:item-list/id 53,
    :item-list/items-id 1,
    :item-list/kind "管制藥品",
    :item-list/object "K他命",
    :item-list/subkind "第三級"}
   {:item-list/id 54,
    :item-list/items-id 1,
    :item-list/kind "管制藥品",
    :item-list/object "Lorazepam(一粒眠)",
    :item-list/subkind "第三級"}
   {:item-list/id 55,
    :item-list/items-id 1,
    :item-list/kind "管制藥品",
    :item-list/object "其他",
    :item-list/subkind "第三級"}
   {:item-list/id 56,
    :item-list/items-id 1,
    :item-list/kind "管制藥品",
    :item-list/object "Alprazolam(蝴蝶片)",
    :item-list/subkind "第四級"}
   {:item-list/id 57,
    :item-list/items-id 1,
    :item-list/kind "管制藥品",
    :item-list/object "Diazepam(安定、煩寧)",
    :item-list/subkind "第四級"}
   {:item-list/id 58,
    :item-list/items-id 1,
    :item-list/kind "管制藥品",
    :item-list/object "其他",
    :item-list/subkind "第四級"}
   {:item-list/id 59,
    :item-list/items-id 1,
    :item-list/kind "管制藥品",
    :item-list/object "管制先驅原料",
    :item-list/subkind "藥事法"}
   {:item-list/id 60,
    :item-list/items-id 1,
    :item-list/kind "管制藥品",
    :item-list/object "這是管制藥品備註",
    :item-list/subkind "其他類備註"}
   {:item-list/id 61,
    :item-list/items-id 1,
    :item-list/kind "槍砲彈藥(刀)械",
    :item-list/object nil,
    :item-list/subkind "制式長槍"}
   {:item-list/id 62,
    :item-list/items-id 1,
    :item-list/kind "槍砲彈藥(刀)械",
    :item-list/object nil,
    :item-list/subkind "制式短槍"}
   {:item-list/id 63,
    :item-list/items-id 1,
    :item-list/kind "槍砲彈藥(刀)械",
    :item-list/object nil,
    :item-list/subkind "土、改造槍"}
   {:item-list/id 64,
    :item-list/items-id 1,
    :item-list/kind "槍砲彈藥(刀)械",
    :item-list/object nil,
    :item-list/subkind "空氣槍"}
   {:item-list/id 65,
    :item-list/items-id 1,
    :item-list/kind "槍砲彈藥(刀)械",
    :item-list/object nil,
    :item-list/subkind "管制刀械"}
   {:item-list/id 66,
    :item-list/items-id 1,
    :item-list/kind "槍砲彈藥(刀)械",
    :item-list/object "這是槍砲彈藥刀械備註",
    :item-list/subkind "其他類備註"}
   {:item-list/id 67,
    :item-list/items-id 1,
    :item-list/kind "豬肉(含肉製品)",
    :item-list/object nil,
    :item-list/subkind nil}
   {:item-list/id 68,
    :item-list/items-id 1,
    :item-list/kind "野保法",
    :item-list/object nil,
    :item-list/subkind nil}])

(def item-people-full
  [{:item-people/id 1,
    :item-people/items-id 1,
    :item-people/kind "危險物品",
    :item-people/people 0,
    :item-people/piece 29}
   {:item-people/id 2,
    :item-people/items-id 1,
    :item-people/kind "危安物品",
    :item-people/people 0,
    :item-people/piece 9}
   {:item-people/id 3,
    :item-people/items-id 1,
    :item-people/kind "管制藥品",
    :item-people/people 0,
    :item-people/piece 20}
   {:item-people/id 4,
    :item-people/items-id 1,
    :item-people/kind "槍砲彈藥(刀)械",
    :item-people/people 0,
    :item-people/piece 5}
   {:item-people/id 5,
    :item-people/items-id 1,
    :item-people/kind "豬肉(含肉製品)",
    :item-people/people 9,
    :item-people/piece 19}
   {:item-people/id 6,
    :item-people/items-id 1,
    :item-people/kind "野保法",
    :item-people/people 8,
    :item-people/piece 18}])

(defn get-child
  [db items-id table]
  (let [sql-map (-> (sqlh/select :*)
                    (sqlh/from table)
                    (sqlh/where [:= :items-id items-id]))]
    (db/honey! db sql-map {})))

(test/deftest import-item-test
  (println (clear-db))
  (test/testing "test import-item-file!"
    (test/is (= json-full-items (items/import-item-file! json-file)))
    (test/is (= all-list-full (get-child @db/sys-db 1 :all-list)))
    (test/is (= item-list-full (get-child @db/sys-db 1 :item-list)))
    (test/is (= item-people-full (get-child @db/sys-db 1 :item-people)))))