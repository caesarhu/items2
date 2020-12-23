(ns items2.db.mail
  (:require [items2.config :as config]
            [datoteka.core :as fs]
            [postal.core :refer [send-message]]
            [taoensso.timbre :as timbre]
            [java-time :as jt]
            [items2.db.items-csv :as csv]
            [items2.db.mail-list :as mail]
            [items2.db.core :as db]
            [items2.utils :as utils]
            [clojure.string :as string]
            [items2.db.items :as items]))

(defn attach-mail-file [path]
  (if (and (string? path) (fs/regular-file? path))
    (let [file (fs/file path)
          file-name (.getName file)]
      {:type :attachment
       :file-name file-name
       :content-type "text/x-csv; charset=utf-8"
       :content file})
    (timbre/log :error ::attach-mail-file-fail path)))

(defn make-mail-data [from to subject content file-paths]
  (let [mail-header {:from    from
                     :to      to
                     :subject subject}
        body-base [{:type "text/plain; charset=utf-8"
                    :content content}]
        attachments (filter some? (map attach-mail-file file-paths))
        body (into body-base attachments)]
    (assoc mail-header :body body)))

(defn send-items-mail [to subject paths]
  (let [from "system@dns.apb.gov.tw"
        content "系統於每日凌晨3時自動寄送前1日危險(安)物品資料，每週二凌晨3時自動寄送前1週(上週二至週一)危險(安)物品資料，請勿直接回信，如有問題請聯絡勤指中心資訊室 736-2222。"
        mail-data (make-mail-data from to subject content paths)]
    (try
      (System/setProperty "mail.mime.splitlongparameters" "false")
      ;; 附件中文檔名必須如此設定，才能讓所有mail client正確識別
      (send-message (:mail-server @config/config) mail-data)
      (timbre/log :info ::send-items-mail-success {:email (string/join ", " to)
                                                   :subject subject})
      (catch Exception ex
        (timbre/log :error ::send-items-mail-fail (str (string/join ", " to) " due to: " (.getMessage ex)))))))

(defn send-period-mail
  ([db period csv-dir]
   (let [sleep-interval 10000
         csv-files (->> (csv/spit-reports-csv db period (:report @config/config) csv-dir)
                        (apply map #(vector %1 %2)))
         mail-list (->> (map val (mail/get-mail-list db))
                        (map (fn [v]
                               (map :email v))))
         subject-fn (fn [s]
                      (-> (re-find #".*/(.*)(\.csv)" s)
                          second
                          ;(string/replace #"[明細|統計]" "查獲危險(安)物品登錄資料")
                          (string/replace #"(明細|統計)" "測試郵件")))
         mail-ready (map #(hash-map :to %1 :files %2 :subject (subject-fn (first %2))) mail-list csv-files)]
     (doseq [mail-data mail-ready]
       (apply send-items-mail ((juxt :to :subject :files) mail-data))
       (Thread/sleep sleep-interval))))
  ([db period]
   (send-period-mail db period (:csv-path @config/config)))
  ([period]
   (send-period-mail @db/sys-db period (:csv-path @config/config))))

(defn daily-mail
  ([db csv-dir json-dir]
   (items/daily-import! db json-dir)
   (let [today (jt/local-date)]
     (send-period-mail db (utils/yesterday) csv-dir)
     (when (jt/tuesday? today)
       (send-period-mail db (utils/last-week) csv-dir))
     (when (= 2 (jt/as today :day-of-month))
       (send-period-mail db (utils/last-month) csv-dir))))
  ([db]
   (daily-mail db (:csv-path @config/config) (:json-file-path @config/config)))
  ([]
   (daily-mail @db/sys-db)))

