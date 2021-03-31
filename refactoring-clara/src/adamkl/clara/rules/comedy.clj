(ns adamkl.clara.rules.comedy
  (:require [clara.rules :as c]
            [adamkl.clara.facts :refer [->LineItem]])
  (:import [adamkl.clara.facts Play Performance]))

(c/defrule comedy-base-amount
  [Play (= :comedy type) (= ?id id)]
  [Performance (= ?id play-id) (= ?audience audience)]
  =>
  (c/insert! (->LineItem ?id :charge (+ 30000 (* 300 ?audience)))))

(c/defrule comedy-bonus-amount
  [Play (= :comedy type) (= ?id id)]
  [Performance (= ?id play-id) (= ?audience audience)]
  [:test (> ?audience 20)]
  =>
  (c/insert! (->LineItem ?id :charge (+ 10000 (* 500 (- ?audience 20))))))

(c/defrule comedy-volume-credits
  [Play (= :comedy type) (= ?id id)]
  [Performance (= ?id play-id) (= ?audience audience)]
  =>
  (c/insert! (->LineItem ?id :credit (+ (max (- ?audience 30) 0)
                                        (int (Math/floor (/ ?audience 5)))))))

