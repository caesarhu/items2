(ns items2.json
  (:require [aave.core :refer [>defn >defn-]]
            [items2.transform :refer [custom-transformer]]
            [clojure.string :as string]
            [malli.core :as m]
            [java-time :as jt]
            [items2.utils :as utils]
            [medley.core :as medley]
            [exoscale.ex :as ex]
            [datoteka.core :as fs]
            [jsonista.core :as json]
            [items2.transform :as t]
            [items2.config :as config]
            [malli.util :as mu]
            [malli.transform :as mt]
            [items2.items-malli :as im]))

(>defn ->int
  [s]
  [string? => int?]
  (Integer/parseInt s))

(>defn parse-unit
  [s]
  [string? => map?]
  (let [[_ 單位 子單位] (re-find #"(^.{4})(.*)" s)]
    {:單位 (utils/trim-space 單位)
     :子單位 (-> (utils/trim-space 子單位)
              utils/bug-unit-translate)}))

(>defn parse-itemlist
  [s]
  [string? => map?]
  (let [[kind sub_kind item] (string/split s #"-")
        result {:項目清單檔/種類 kind :項目清單檔/類別 sub_kind :項目清單檔/物品 item}]
    (utils/translate-map result (config/meta-dict))))

(>defn parse-people
  [s]
  [string? => map?]
  (let [[kind piece people] (string/split s #"-")
        people-count (-> (re-find #"\d+" people)
                         ->int)
        piece-count (-> (re-find #"\d+" piece)
                        ->int)
        result {:項目人數檔/種類 kind :項目人數檔/件數 piece-count :項目人數檔/人數 people-count}]
    (utils/translate-map result (config/meta-dict))))

(>defn parse-all-list
  [m]
  [map? => seq?]
  (map (fn [entry]
         (let [k (key entry)
               v (val entry)
               result {:所有項目檔/項目 k
                       :所有項目檔/數量 (->int v)}]
           (utils/translate-map result (config/meta-dict))))
       m))

(def item-transformer
  (mt/transformer
    t/custom-transformer
    (mt/key-transformer {:decode keyword})))

(def item-json
  [:map
   ["項目清單"
    [:vector [string? {:transfer parse-itemlist}]]]
   ["項目人數" [:vector [string? {:transfer parse-people}]]]
   ["所有項目數量" {:transfer parse-all-list}
    [:map-of string? string?]]
   ["日期" string?]
   ["員警姓名" [string? {:transfer utils/trim-space}]]
   ["處理情形" string?]
   ["攜帶方式" string?]
   ["班次" string?]
   ["航空貨運業者簽章" {:optional true} [:maybe string?]]
   ["IpAddress" {:optional true} [:maybe string?]]
   ["所有備註" {:optional true} [:maybe string?]]
   ["旅客簽章" {:optional true} [:maybe string?]]
   ["查獲人簽章" {:optional true} [:maybe string?]]
   ["勤務單位" [string? {:transfer parse-unit}]]
   ["查獲位置" {:optional true} [:maybe string?]]
   ["時間" string?]
   ["旅客護照號碼/身分證號" {:optional true} [:maybe string?]]])

(def qualified-json
  (utils/qualify-malli (->> (mu/select-keys im/malli-items [:id])
                            mu/optional-keys
                            (mu/merge im/malli-items))
                       :items))

(>defn item-parser
  [m]
  [map? => any?]
  (let [mk (medley/map-keys keyword m)
        {:keys [日期 時間 勤務單位]} mk
        datetime (jt/local-date-time (string/join "T" [日期 時間]))
        result (-> mk
                   (assoc :查獲時間 datetime)
                   (merge 勤務單位))]
    (utils/translate-map result (config/json-dict))))

(>defn json-parser
  [file-name]
  [string? => map?]
  (let [file (fs/file file-name)
        json (json/read-value file)
        ftime (utils/file-time file)]
    (->> (m/decode item-json json t/custom-transformer)
         item-parser
         (merge {:原始檔案(.getName file) :原始檔案時間 ftime})
         (medley/map-keys #(keyword "危安物品檔" (name %)))
         (medley/map-keys utils/mata-translate))))
