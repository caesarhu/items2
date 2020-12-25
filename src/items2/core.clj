(ns items2.core
  (:gen-class)
  (:require [juxt.clip.repl :as clip]
            [items2.config :as config]
            [items2.db.mail :as mail]))

(clip/set-init! (fn [] @config/config))
(clip/start)

(defn send-mail
  "每日寄送危安物品登錄APP mail"
  [& args]
  (println "開始寄送危安物品登錄APP mail !")
  (mail/daily-mail)
  (println "結束寄送危安物品登錄APP mail !")
  (clip/stop))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
