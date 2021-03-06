(ns items2.json
  (:require
    [aave.core :refer [>defn >defn-]]
    [clojure.string :as string]
    [datoteka.core :as fs]
    [exoscale.ex :as ex]
    [items2.config :as config]
    [items2.items-malli :as im]
    [items2.transform :refer [custom-transformer]]
    [items2.transform :as t]
    [items2.utils :as utils]
    [java-time :as jt]
    [jsonista.core :as json]
    [malli.core :as m]
    [malli.error :as me]
    [malli.transform :as mt]
    [medley.core :as medley]
    [taoensso.timbre :as timbre]))


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
       [map? => vector?]
       (vec (map (fn [entry]
                   (let [k (key entry)
                         v (val entry)
                         result {:所有項目檔/項目 k
                                 :所有項目檔/數量 (utils/str->int v)}]
                     (utils/translate-map result @config/meta-dict)))
                 m)))


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


(defn make-child-schema
  [c-key]
  (let [schema (get im/items-malli c-key)]
    (utils/dissoc-qualified-schema schema c-key :id :items-id)))


(def parsed-item-schema
  [:map
   [:all-list
    [:vector
     (make-child-schema :all-list)]]
   [:item-people
    [:vector
     (make-child-schema :item-people)]]
   [:item-list
    [:vector
     (make-child-schema :item-list)]]
   [:item item-schema]])


(ex/derive ::json-parser ::ex/incorrect)


(defn json-parser
  [file-name]
  (let [file (slurp file-name) 
        json (m/decode item-json (json/read-value file) json-transformer)
        {:keys [日期 時間 勤務單位]} json
        datetime (jt/local-date-time (string/join "T" [日期 時間]))
        ftime (utils/file-time file)
        raw-json (->> (dissoc json :日期 :時間 :勤務單位)
                      (merge 勤務單位 {:查獲時間 datetime :原始檔案 (.getName file) :原始檔案時間 ftime})
                      (medley/map-keys #(utils/qualify-key "危安物品檔" %))
                      (medley/map-keys utils/meta-translate)
                      (medley/map-keys utils/json-translate))
        tables (:items-child @config/config)
        result (merge {:item (apply dissoc raw-json tables)} (select-keys raw-json tables))]
    (if (m/validate parsed-item-schema result)
      result
      (let [explain (me/humanize (m/explain parsed-item-schema result))]
        (timbre/log :error
                    "json file incorrect! "
                    {:from ::json-parser
                     :file file-name
                     :explain explain
                     :item result})
        (throw (ex/ex-info (str "json file incorrect! " explain)
                           ::json-parser
                           {:from ::json-parser
                            :file file-name}))))))

