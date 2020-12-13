(ns items2.json-test
  (:require [clojure.test :refer :all]
            [items2.json :refer :all]
            [items2.helpers :refer :all]
            [java-time :as jt]))

(use-fixtures
  :once
  instrument-specs)

(def json-file "dev/resources/data/full.json")

(def full-json
  {:危安物品檔/所有項目數量 '({:all-list/item "高爾夫球桿", :all-list/quantity 1}
                   {:all-list/item "電池酸液", :all-list/quantity 1}
                   {:all-list/item "棍棒、工具機及農具類", :all-list/quantity 1}
                   {:all-list/item "白磷、黃磷", :all-list/quantity 1}
                   {:all-list/item "有機過氧化物B型態", :all-list/quantity 1}
                   {:all-list/item "尖銳物品類", :all-list/quantity 1}
                   {:all-list/item "小白板", :all-list/quantity 1}
                   {:all-list/item "煤油", :all-list/quantity 1}
                   {:all-list/item "乙醚", :all-list/quantity 1}
                   {:all-list/item "安非他命", :all-list/quantity 1}
                   {:all-list/item "酒", :all-list/quantity 1}
                   {:all-list/item "FM2", :all-list/quantity 1}
                   {:all-list/item "石頭", :all-list/quantity 1}
                   {:all-list/item "磁性物質", :all-list/quantity 1}
                   {:all-list/item "鐳", :all-list/quantity 1}
                   {:all-list/item "砒霜、農藥", :all-list/quantity 1}
                   {:all-list/item "汞", :all-list/quantity 1}
                   {:all-list/item "古柯鹼", :all-list/quantity 1}
                   {:all-list/item "仙女棒", :all-list/quantity 1}
                   {:all-list/item "鴉片", :all-list/quantity 1}
                   {:all-list/item "制式長槍", :all-list/quantity 1}
                   {:all-list/item "大麻", :all-list/quantity 1}
                   {:all-list/item "砷", :all-list/quantity 1}
                   {:all-list/item "甲苯", :all-list/quantity 1}
                   {:all-list/item "石灰", :all-list/quantity 1}
                   {:all-list/item "MDMA", :all-list/quantity 1}
                   {:all-list/item "其他", :all-list/quantity 1}
                   {:all-list/item "Alprazolam(蝴蝶片)",
                    :all-list/quantity 1}
                   {:all-list/item "滑板", :all-list/quantity 1}
                   {:all-list/item "鎂粉", :all-list/quantity 1}
                   {:all-list/item "管制先驅原料", :all-list/quantity 1}
                   {:all-list/item "Diazepam(安定、煩寧)",
                    :all-list/quantity 1}
                   {:all-list/item "Lorazepam(一粒眠)",
                    :all-list/quantity 1}
                   {:all-list/item "球棒", :all-list/quantity 1}
                   {:all-list/item "海洛因", :all-list/quantity 1}
                   {:all-list/item "醫療廢棄物", :all-list/quantity 1}
                   {:all-list/item "刀剪類", :all-list/quantity 1}
                   {:all-list/item "高壓罐", :all-list/quantity 100}
                   {:all-list/item "漂白水", :all-list/quantity 1}
                   {:all-list/item "液狀、膠狀及噴霧物品類", :all-list/quantity 1}
                   {:all-list/item "汽油", :all-list/quantity 1}
                   {:all-list/item "制式短槍", :all-list/quantity 1}
                   {:all-list/item "LSD", :all-list/quantity 1}
                   {:all-list/item "玩具槍類", :all-list/quantity 1}
                   {:all-list/item "空氣槍", :all-list/quantity 1}
                   {:all-list/item "土、改造槍", :all-list/quantity 1}
                   {:all-list/item "爆竹", :all-list/quantity 1}
                   {:all-list/item "丁基原啡因", :all-list/quantity 1}
                   {:all-list/item "硫酸(含酸超過51％)", :all-list/quantity 1}
                   {:all-list/item "嗎啡", :all-list/quantity 1}
                   {:all-list/item "K他命", :all-list/quantity 1}
                   {:all-list/item "漆類", :all-list/quantity 1}
                   {:all-list/item "打火機", :all-list/quantity 10}
                   {:all-list/item "鈾", :all-list/quantity 1}
                   {:all-list/item "硝酸鈉", :all-list/quantity 1}
                   {:all-list/item "鋰電池", :all-list/quantity 1000}
                   {:all-list/item "鈽", :all-list/quantity 1}
                   {:all-list/item "環境危害物質", :all-list/quantity 1}
                   {:all-list/item "活性碳", :all-list/quantity 1}
                   {:all-list/item "管制刀械", :all-list/quantity 1}),
   :items/file-time (jt/local-date-time "2020-12-09T19:53:22")
   :items/ip "0.0.0.0",
   :items/passenger-sign "2020-11-24-11-27-18.986-passengerSign.jpg",
   :items/unit "刑警大隊",
   :items/trader-sign "2020-11-24-11-27-18.986-substituteCadreSign.jpg",
   :危安物品檔/項目人數 [{:item-people/kind "危險物品",
                 :item-people/piece 29,
                 :item-people/people 0}
                {:item-people/kind "危安物品",
                 :item-people/piece 9,
                 :item-people/people 0}
                {:item-people/kind "管制藥品",
                 :item-people/piece 20,
                 :item-people/people 0}
                {:item-people/kind "槍砲彈藥(刀)械",
                 :item-people/piece 5,
                 :item-people/people 0}
                {:item-people/kind "豬肉(含肉製品)",
                 :item-people/piece 19,
                 :item-people/people 9}
                {:item-people/kind "野保法",
                 :item-people/piece 18,
                 :item-people/people 8}],
   :items/check-sign "2020-11-24-11-27-18.986-censorSign.jpg",
   :危安物品檔/日期 "2020-11-24",
   :items/process "自願放棄",
   :items/file "full.json",
   :items/check-time (jt/local-date-time "2020-11-24T11:20:32.302"),
   :items/check-line "國際線",
   :items/passenger-id "A123456789",
   :危安物品檔/勤務單位 {:單位 "刑警大隊", :子單位 "偵一隊"},
   :items/flight "AAA",
   :items/carry "手提",
   :危安物品檔/項目清單 [{:item-list/kind "危險物品",
                 :item-list/subkind "第一類爆炸性物品",
                 :item-list/object "爆竹"}
                {:item-list/kind "危險物品",
                 :item-list/subkind "第一類爆炸性物品",
                 :item-list/object "仙女棒"}
                {:item-list/kind "危險物品",
                 :item-list/subkind "第二類危險氣體",
                 :item-list/object "打火機"}
                {:item-list/kind "危險物品",
                 :item-list/subkind "第二類危險氣體",
                 :item-list/object "高壓罐"}
                {:item-list/kind "危險物品",
                 :item-list/subkind "第三類易燃性液體",
                 :item-list/object "漆類"}
                {:item-list/kind "危險物品",
                 :item-list/subkind "第三類易燃性液體",
                 :item-list/object "汽油"}
                {:item-list/kind "危險物品",
                 :item-list/subkind "第三類易燃性液體",
                 :item-list/object "煤油"}
                {:item-list/kind "危險物品",
                 :item-list/subkind "第三類易燃性液體",
                 :item-list/object "甲苯"}
                {:item-list/kind "危險物品",
                 :item-list/subkind "第三類易燃性液體",
                 :item-list/object "乙醚"}
                {:item-list/kind "危險物品",
                 :item-list/subkind "第三類易燃性液體",
                 :item-list/object "酒"}
                {:item-list/kind "危險物品",
                 :item-list/subkind "第四類易燃固體、自燃物質、遇水釋放易燃氣體之物質",
                 :item-list/object "活性碳"}
                {:item-list/kind "危險物品",
                 :item-list/subkind "第四類易燃固體、自燃物質、遇水釋放易燃氣體之物質",
                 :item-list/object "白磷、黃磷"}
                {:item-list/kind "危險物品",
                 :item-list/subkind "第四類易燃固體、自燃物質、遇水釋放易燃氣體之物質",
                 :item-list/object "鎂粉"}
                {:item-list/kind "危險物品",
                 :item-list/subkind "第五類氧化物及有機過氧化物",
                 :item-list/object "硝酸鈉"}
                {:item-list/kind "危險物品",
                 :item-list/subkind "第五類氧化物及有機過氧化物",
                 :item-list/object "漂白水"}
                {:item-list/kind "危險物品",
                 :item-list/subkind "第五類氧化物及有機過氧化物",
                 :item-list/object "有機過氧化物B型態"}
                {:item-list/kind "危險物品",
                 :item-list/subkind "第六類毒性物質及傳染性物質",
                 :item-list/object "砷"}
                {:item-list/kind "危險物品",
                 :item-list/subkind "第六類毒性物質及傳染性物質",
                 :item-list/object "砒霜、農藥"}
                {:item-list/kind "危險物品",
                 :item-list/subkind "第六類毒性物質及傳染性物質",
                 :item-list/object "醫療廢棄物"}
                {:item-list/kind "危險物品",
                 :item-list/subkind "第七類放射性物質",
                 :item-list/object "鈾"}
                {:item-list/kind "危險物品",
                 :item-list/subkind "第七類放射性物質",
                 :item-list/object "鈽"}
                {:item-list/kind "危險物品",
                 :item-list/subkind "第七類放射性物質",
                 :item-list/object "鐳"}
                {:item-list/kind "危險物品",
                 :item-list/subkind "第八類腐蝕性物質",
                 :item-list/object "硫酸(含酸超過51％)"}
                {:item-list/kind "危險物品",
                 :item-list/subkind "第八類腐蝕性物質",
                 :item-list/object "電池酸液"}
                {:item-list/kind "危險物品",
                 :item-list/subkind "第八類腐蝕性物質",
                 :item-list/object "汞"}
                {:item-list/kind "危險物品",
                 :item-list/subkind "第八類腐蝕性物質",
                 :item-list/object "石灰"}
                {:item-list/kind "危險物品",
                 :item-list/subkind "第九類其他",
                 :item-list/object "鋰電池"}
                {:item-list/kind "危險物品",
                 :item-list/subkind "第九類其他",
                 :item-list/object "磁性物質"}
                {:item-list/kind "危險物品",
                 :item-list/subkind "第九類其他",
                 :item-list/object "環境危害物質"}
                {:item-list/kind "危安物品",
                 :item-list/subkind "刀剪類",
                 :item-list/object nil}
                {:item-list/kind "危安物品",
                 :item-list/subkind "尖銳物品類",
                 :item-list/object nil}
                {:item-list/kind "危安物品",
                 :item-list/subkind "棍棒、工具機及農具類",
                 :item-list/object nil}
                {:item-list/kind "危安物品",
                 :item-list/subkind "玩具槍類",
                 :item-list/object nil}
                {:item-list/kind "危安物品",
                 :item-list/subkind "液狀、膠狀及噴霧物品類",
                 :item-list/object nil}
                {:item-list/kind "危安物品",
                 :item-list/subkind "石頭",
                 :item-list/object nil}
                {:item-list/kind "危安物品",
                 :item-list/subkind "運動用品類",
                 :item-list/object "球棒"}
                {:item-list/kind "危安物品",
                 :item-list/subkind "運動用品類",
                 :item-list/object "高爾夫球桿"}
                {:item-list/kind "危安物品",
                 :item-list/subkind "運動用品類",
                 :item-list/object "滑板"}
                {:item-list/kind "危安物品",
                 :item-list/subkind "其他類備註",
                 :item-list/object "這是危安物品備註"}
                {:item-list/kind "管制藥品",
                 :item-list/subkind "第一級",
                 :item-list/object "海洛因"}
                {:item-list/kind "管制藥品",
                 :item-list/subkind "第一級",
                 :item-list/object "嗎啡"}
                {:item-list/kind "管制藥品",
                 :item-list/subkind "第一級",
                 :item-list/object "鴉片"}
                {:item-list/kind "管制藥品",
                 :item-list/subkind "第一級",
                 :item-list/object "古柯鹼"}
                {:item-list/kind "管制藥品",
                 :item-list/subkind "第一級",
                 :item-list/object "其他"}
                {:item-list/kind "管制藥品",
                 :item-list/subkind "第二級",
                 :item-list/object "安非他命"}
                {:item-list/kind "管制藥品",
                 :item-list/subkind "第二級",
                 :item-list/object "MDMA"}
                {:item-list/kind "管制藥品",
                 :item-list/subkind "第二級",
                 :item-list/object "大麻"}
                {:item-list/kind "管制藥品",
                 :item-list/subkind "第二級",
                 :item-list/object "LSD"}
                {:item-list/kind "管制藥品",
                 :item-list/subkind "第二級",
                 :item-list/object "其他"}
                {:item-list/kind "管制藥品",
                 :item-list/subkind "第三級",
                 :item-list/object "FM2"}
                {:item-list/kind "管制藥品",
                 :item-list/subkind "第三級",
                 :item-list/object "小白板"}
                {:item-list/kind "管制藥品",
                 :item-list/subkind "第三級",
                 :item-list/object "丁基原啡因"}
                {:item-list/kind "管制藥品",
                 :item-list/subkind "第三級",
                 :item-list/object "K他命"}
                {:item-list/kind "管制藥品",
                 :item-list/subkind "第三級",
                 :item-list/object "Lorazepam(一粒眠)"}
                {:item-list/kind "管制藥品",
                 :item-list/subkind "第三級",
                 :item-list/object "其他"}
                {:item-list/kind "管制藥品",
                 :item-list/subkind "第四級",
                 :item-list/object "Alprazolam(蝴蝶片)"}
                {:item-list/kind "管制藥品",
                 :item-list/subkind "第四級",
                 :item-list/object "Diazepam(安定、煩寧)"}
                {:item-list/kind "管制藥品",
                 :item-list/subkind "第四級",
                 :item-list/object "其他"}
                {:item-list/kind "管制藥品",
                 :item-list/subkind "藥事法",
                 :item-list/object "管制先驅原料"}
                {:item-list/kind "管制藥品",
                 :item-list/subkind "其他類備註",
                 :item-list/object "這是管制藥品備註"}
                {:item-list/kind "槍砲彈藥(刀)械",
                 :item-list/subkind "制式長槍",
                 :item-list/object nil}
                {:item-list/kind "槍砲彈藥(刀)械",
                 :item-list/subkind "制式短槍",
                 :item-list/object nil}
                {:item-list/kind "槍砲彈藥(刀)械",
                 :item-list/subkind "土、改造槍",
                 :item-list/object nil}
                {:item-list/kind "槍砲彈藥(刀)械",
                 :item-list/subkind "空氣槍",
                 :item-list/object nil}
                {:item-list/kind "槍砲彈藥(刀)械",
                 :item-list/subkind "管制刀械",
                 :item-list/object nil}
                {:item-list/kind "槍砲彈藥(刀)械",
                 :item-list/subkind "其他類備註",
                 :item-list/object "這是槍砲彈藥刀械備註"}
                {:item-list/kind "豬肉(含肉製品)",
                 :item-list/subkind nil,
                 :item-list/object nil}
                {:item-list/kind "野保法",
                 :item-list/subkind nil,
                 :item-list/object nil}],
   :items/subunit "偵一隊",
   :items/police "大強子",
   :items/memo "這是全部備註",
   :危安物品檔/時間 "11:20:32.302"})