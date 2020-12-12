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
            [items2.config :as config]))

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
        result {:種類 kind :類別 sub_kind :物品 item}]
    result))

(>defn parse-people
  [s]
  [string? => map?]
  (let [[kind piece people] (string/split s #"-")
        people-count (-> (re-find #"\d+" people)
                         ->int)
        piece-count (-> (re-find #"\d+" piece)
                        ->int)
        result {:種類 kind :件數 piece-count :人數 people-count}]
    result))

(def item-json
  [:map
   ["項目清單" [:vector [string? {:transfer parse-itemlist}]]]
   ["項目人數" [:vector [string? {:transfer parse-people}]]]
   ["所有項目數量"
    [:map-of :string [string? {:transfer ->int}]]]
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

(>defn item-parser
  [m]
  [map? => any?]
  (let [mk (medley/map-keys keyword m)
        {:keys [日期 時間 所有項目數量 勤務單位 項目清單 項目人數]} mk
        all-v (map (fn [entry]
                     (let [k (key entry)
                           v (val entry)]
                       {:項目 k
                        :數量 v}))
                   所有項目數量)
        item-list (->> (map #(utils/qualify-map % :項目清單檔) 項目清單)
                       (map #(utils/translate-map % (config/meta-dict))))
        item-people (->> (map #(utils/qualify-map % :項目人數檔) 項目人數)
                         (map #(utils/translate-map % (config/meta-dict))))
        all-list (->> (map #(utils/qualify-map % :所有項目檔) all-v)
                      (map #(utils/translate-map % (config/meta-dict))))
        datetime (jt/local-date-time (string/join "T" [日期 時間]))
        result (-> mk
                   (assoc :all-list all-list :查獲時間 datetime)
                   (merge 勤務單位))]
    (-> (utils/translate-map result (config/json-dict))
        (assoc :item-list item-list :item-people item-people))))

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
