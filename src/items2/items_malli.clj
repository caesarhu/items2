(ns items2.items-malli
  (:require [hodur-translate.spec.malli-schemas :refer [local-date local-date-time]]))

;;; auto generate by hodur

(def malli-item-people
  [:map
   [:id clojure.core/pos-int?]
   [:items-id clojure.core/pos-int?]
   [:kind clojure.core/string?]
   [:people {:optional true} [:maybe clojure.core/integer?]]
   [:piece clojure.core/integer?]])


(def malli-item-list
  [:map
   [:id clojure.core/pos-int?]
   [:items-id clojure.core/pos-int?]
   [:kind clojure.core/string?]
   [:object {:optional true} [:maybe clojure.core/string?]]
   [:subkind {:optional true} [:maybe clojure.core/string?]]])


(def malli-units
  [:map
   [:id clojure.core/pos-int?]
   [:subunit {:optional true} [:maybe clojure.core/string?]]
   [:unit clojure.core/string?]])


(def malli-mail-list
  [:map
   [:email clojure.core/string?]
   [:id clojure.core/pos-int?]
   [:memo {:optional true} [:maybe clojure.core/string?]]
   [:name {:optional true} [:maybe clojure.core/string?]]
   [:position {:optional true} [:maybe clojure.core/string?]]
   [:subunit {:optional true} [:maybe clojure.core/string?]]
   [:unit clojure.core/string?]])


(def malli-items
  [:map
   [:carry clojure.core/string?]
   [:check-line {:optional true} [:maybe clojure.core/string?]]
   [:check-sign {:optional true} [:maybe clojure.core/string?]]
   [:check-time local-date-time]
   [:file clojure.core/string?]
   [:file-time local-date-time]
   [:flight {:optional true} [:maybe clojure.core/string?]]
   [:id clojure.core/pos-int?]
   [:ip {:optional true} [:maybe clojure.core/string?]]
   [:memo {:optional true} [:maybe clojure.core/string?]]
   [:passenger-id {:optional true} [:maybe clojure.core/string?]]
   [:passenger-sign {:optional true} [:maybe clojure.core/string?]]
   [:police clojure.core/string?]
   [:process clojure.core/string?]
   [:subunit {:optional true} [:maybe clojure.core/string?]]
   [:trader-sign {:optional true} [:maybe clojure.core/string?]]
   [:unit clojure.core/string?]])


(def malli-last-time
  [:map
   [:fail clojure.core/integer?]
   [:file-time local-date-time]
   [:id clojure.core/pos-int?]
   [:success clojure.core/integer?]
   [:total clojure.core/integer?]])


(def malli-all-list
  [:map
   [:id clojure.core/pos-int?]
   [:item clojure.core/string?]
   [:items-id clojure.core/pos-int?]
   [:quantity clojure.core/integer?]])


(def malli-ipad
  [:map
   [:id clojure.core/pos-int?]
   [:ip clojure.core/string?]
   [:ipad-name clojure.core/string?]
   [:subunit {:optional true} [:maybe clojure.core/string?]]
   [:unit clojure.core/string?]])
