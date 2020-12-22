(ns items2.mail
  (:require [items2.config :as config]
            [datoteka.core :as fs]
            [postal.core :refer [send-message]]))

(defn attach-mail-file [path]
  (if (and (string? path) (fs/regular-file? path))
    (let [file (fs/file path)
          file-name (.getName file)]
      {:type :attachment
       :file-name file-name
       :content-type "text/x-csv; charset=utf-8"
       :content file})
    (log :error ::attach-mail-file-fail path)))

(defn make-mail-data [from to subject content & file-paths]
  (let [mail-header {:from    from
                     :to      to
                     :subject subject}
        body-base [{:type "text/plain; charset=utf-8"
                    :content content}]
        attachments (filter some? (map attach-mail-file file-paths))
        body (into body-base attachments)]
    (assoc mail-header :body body)))

(defn send-items-mail [to subject & paths]
  (let [from "system@dns.apb.gov.tw"
        content "系統於每日凌晨3時自動寄送前1日危險(安)物品資料，每週二凌晨3時自動寄送前1週(上週二至週一)危險(安)物品資料，請勿直接回信，如有問題請聯絡勤指中心資訊室 736-2222。"
        mail-data (apply make-mail-data from to subject content paths)]
    (try
      (System/setProperty "mail.mime.splitlongparameters" "false")
      ;; 附件中文檔名必須如此設定，才能讓所有mail client正確識別
      (send-message (mail-config) mail-data)
      (log :info ::send-items-mail-success to)
      (catch Exception ex
        (log :error ::send-items-mail-fail (str to " due to: " (.getMessage ex)))))))