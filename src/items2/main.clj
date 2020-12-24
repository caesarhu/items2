(ns items2.main
  (:gen-class)
  (:require [items2.db.mail :refer [daily-mail]]))


(defn -main
  "每日寄送危安物品登錄APP mail"
  [& args]
  (println "開始寄送危安物品登錄APP mail !")
  ;(daily-mail)
  (println (str "args: " args)))
