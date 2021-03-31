(ns adamkl.clara.rules.base
  (:require [clara.rules :as c]
            [clara.rules.accumulators :as a]
            [adamkl.clara.facts :refer [->Total]])
  (:import [adamkl.clara.facts LineItem Total]))

(c/defrule line-item-total
  [?total <- (a/sum :value) :from [LineItem (= ?id id) (= ?type type)]]
  =>
  (c/insert! (->Total ?id ?type ?total)))

(c/defrule grand-total
  [?total <- (a/sum :value) :from [LineItem (= ?type type)]]
  =>
  (c/insert! (->Total :grand ?type ?total)))

(declare get-line-items)
(c/defquery get-line-items
  []
  [?line-item <- LineItem])

(declare get-totals)
(c/defquery get-totals
  []
  [?total <- Total])