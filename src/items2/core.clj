(ns items2.core
  (:gen-class)
  (:require [items2.db.mail :refer [daily-mail]]))


(defn -main
  "每日寄送危安物品登錄APP mail"
  [& args]
  (println "開始寄送危安物品登錄APP mail !")
  (daily-mail))
