(ns items2.json
  (:require [aave.core :refer [>defn >defn-]]
            [items2.transform :refer [custom-transformer]]
            [hodur-translate.spec.malli-schemas :refer [local-date local-date-time]]
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
                         utils/str->int)
        piece-count (-> (re-find #"\d+" piece)
                        utils/str->int)
        result {:項目人數檔/種類 kind :項目人數檔/件數 piece-count :項目人數檔/人數 people-count}]
    (utils/translate-map result @config/meta-dict)))

(>defn parse-all-list
  [m]
  [map? => seq?]
  (map (fn [entry]
         (let [k (key entry)
               v (val entry)
               result {:所有項目檔/項目 k
                       :所有項目檔/數量 (utils/str->int v)}]
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

(def parsed-item-schema
  [:map
   [:items/file-time local-date-time]
   [:items/ip {:optional true} [:maybe string?]]
   [:items/passenger-sign {:optional true} [:maybe string?]]
   [:items/unit string?]
   [:items/trader-sign {:optional true} [:maybe string?]]
   [:items/check-sign string?]
   [:items/process string?]
   [:items/file string?]
   [:all-list
    [:sequential
     [:map [:all-list/item string?] [:all-list/quantity int?]]]]
   [:items/check-time local-date-time]
   [:items/check-line {:optional true} [:maybe string?]]
   [:item-people
    [:vector
     [:map
      [:item-people/kind string?]
      [:item-people/piece int?]
      [:item-people/people {:optional true} [:maybe int?]]]]]
   [:items/passenger-id {:optional true} [:maybe string?]]
   [:items/flight string?]
   [:item-list
    [:vector
     [:map
      [:item-list/kind string?]
      [:item-list/subkind {:optional true} [:maybe string?]]
      [:item-list/object {:optional true} [:maybe string?]]]]]
   [:items/carry string?]
   [:items/subunit {:optional true} [:maybe string?]]
   [:items/police string?]
   [:items/memo {:optional true} [:maybe string?]]])

(>defn json-parser
  [file-name]
  [[:or string? [:fn #(fs/file? %)]] => map?]
  (ex/try+
    (let [file (if (fs/file? file-name)
                 file-name
                 (fs/file file-name))
          json (m/decode item-json (json/read-value file) json-transformer)
          {:keys [日期 時間 勤務單位]} json
          datetime (jt/local-date-time (string/join "T" [日期 時間]))
          ftime (utils/file-time file)
          result (->> (dissoc json :日期 :時間 :勤務單位)
                      (merge 勤務單位 {:查獲時間 datetime :原始檔案 (.getName file) :原始檔案時間 ftime})
                      (medley/map-keys #(keyword "危安物品檔" (name %)))
                      (medley/map-keys utils/mata-translate)
                      (medley/map-keys utils/json-translate))]
      (when-not (m/validate parsed-item-schema result)
        (timbre/log :error ::json-parser {:explain
                                          (-> (m/explain parsed-item-schema parsed-item-schema)
                                              me/humanize)
                                          :item result}))
      (utils/validate-throw parsed-item-schema result))
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

