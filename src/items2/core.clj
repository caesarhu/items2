(ns items2.core
  (:gen-class)
  (:require [juxt.clip.repl :refer [start stop set-init! reset system] :as clip]
            [items2.config :as config]
            [items2.db.items :as items]
            [items2.db.mail :as mail]))

(defn send-mail
  "每日寄送危安物品登錄APP mail"
  [& args]
  (clip/set-init! (fn [] @config/config))
  (clip/start)
  (println "開始寄送危安物品登錄APP mail !")
  (mail/daily-mail)
  (println "結束寄送危安物品登錄APP mail !"))

(defn -main
  "每日寄送危安物品登錄APP mail"
  [& args]
  (send-mail args))
