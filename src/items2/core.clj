(ns items2.core
  (:gen-class)
  (:require [juxt.clip.repl :as clip]
            [items2.config :as config]
            [datoteka.core :as fs]
            [jsonista.core :as json]
            [items2.db.mail :as mail]))

(defn send-mail
  "每日寄送危安物品登錄APP mail"
  [& args]
  (clip/set-init! (fn [] @config/config))
  (clip/start)
  (println "開始寄送危安物品登錄APP mail !")
  (mail/daily-mail)
  (println "結束寄送危安物品登錄APP mail !")
  (clip/stop))

(defn test-read-file
  "每日寄送危安物品登錄APP mail"
  [& args]
  (let [json-file "/home/shun/sftp/upload/2020-12-25-07-43-10.581-DataStore.json"
        file (fs/file json-file)]
    (clip/set-init! (fn [] @config/config))
    (clip/start)
    (println (json/read-value file))
    (clip/stop)))

(defn -main
  "每日寄送危安物品登錄APP mail"
  [& args]
  (send-mail args))
