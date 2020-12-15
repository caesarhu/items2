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
            [items2.items-malli :as im]
            [taoensso.timbre :as timbre]
            [malli.error :as me]))

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
    (utils/translate-map result @config/meta-dict)))

(>defn parse-people
  [s]
  [string? => map?]
  (let [[kind piece people] (string/split s #"-")
        people-count (-> (re-find #"\d+" people)
                         ->int)
        piece-count (-> (re-find #"\d+" piece)
                        ->int)
        result {:項目人數檔/種類 kind :項目人數檔/件數 piece-count :項目人數檔/人數 people-count}]
    (utils/translate-map result @config/meta-dict)))

(>defn parse-all-list
  [m]
  [map? => seq?]
  (map (fn [entry]
         (let [k (key entry)
               v (val entry)
               result {:所有項目檔/項目 k
                       :所有項目檔/數量 (->int v)}]
           (utils/translate-map result @config/meta-dict)))
       m))

(def item-json
  [:map
   [:項目清單
    [:vector [string? {:transfer parse-itemlist}]]]
   [:項目人數 [:vector [string? {:transfer parse-people}]]]
   [:所有項目數量 {:transfer parse-all-list}
    [:map-of string? string?]]
   [:日期 string?]
   [:員警姓名 [string? {:transfer utils/trim-space}]]
   [:處理情形 string?]
   [:攜帶方式 string?]
   [:班次 string?]
   [:航空貨運業者簽章 {:optional true} [:maybe string?]]
   [:IpAddress {:optional true} [:maybe string?]]
   [:所有備註 {:optional true} [:maybe string?]]
   [:旅客簽章 {:optional true} [:maybe string?]]
   [:查獲人簽章 {:optional true} [:maybe string?]]
   [:勤務單位 [string? {:transfer parse-unit}]]
   [:查獲位置 {:optional true} [:maybe string?]]
   [:時間 string?]
   [:旅客護照號碼/身分證號 {:optional true} [:maybe string?]]])

(def raw-json-transformer
  (mt/key-transformer {:decode #(utils/json-translate (keyword %))}))

(def json-transformer
  (mt/transformer
    raw-json-transformer
    t/custom-transformer))

(def item-schema
  (utils/optional-id-schema (:items im/items-malli)))

(def all-list-schema
  (utils/dissoc-schema (:all-list im/items-malli) :all-list/id :all-list/items-id))

(def item-list-schema
  (utils/dissoc-schema (:item-list im/items-malli) :item-list/id :item-list/items-id))

(def item-people-schema
  (utils/dissoc-schema (:item-people im/items-malli) :item-people/id :item-people/items-id))

(defn json-validate
  [{:keys [all-list item-list item-people] :as json}]
  (mapv #(utils/validate-throw all-list-schema %) all-list)
  (mapv #(utils/validate-throw item-list-schema %) item-list)
  (mapv #(utils/validate-throw item-people-schema %) item-people)
  (utils/validate-throw item-schema json))

(>defn json-parser
  [file-name]
  [string? => map?]
  (ex/try+
    (let [file (fs/file file-name)
          json (m/decode item-json (json/read-value file) json-transformer)
          {:keys [日期 時間 勤務單位]} json
          datetime (jt/local-date-time (string/join "T" [日期 時間]))
          ftime (utils/file-time file)
          result (->> (dissoc json :日期 :時間 :勤務單位)
                      (merge 勤務單位 {:查獲時間 datetime :原始檔案 (.getName file) :原始檔案時間 ftime})
                      (medley/map-keys #(keyword "危安物品檔" (name %)))
                      (medley/map-keys utils/mata-translate)
                      (medley/map-keys utils/json-translate))]
      (json-validate result))
    (catch ::ex/incorrect data
      (timbre/log :error ::json-parser data)
      (throw (ex/ex-info (:message data)
                         ::ex/incorrect
                         (merge data {:from ::json-parser}))))
    (catch Throwable e
      (timbre/log :error ::json-parser (utils/ex-cause-and-msg e))
      (throw (ex/ex-info (utils/ex-cause-and-msg e)
                         ::ex/fault
                         {:from ::json-parser}
                         e)))))

