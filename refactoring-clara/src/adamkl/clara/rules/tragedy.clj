(ns adamkl.clara.rules.tragedy
  (:require [clara.rules :as c]
            [adamkl.clara.facts :refer [->LineItem]])
  (:import [adamkl.clara.facts Play Performance]))

(c/defrule tragedy-base-amount
  [Play (= :tragedy type) (= ?id id)]
  [Performance (= ?id play-id) (= ?audience audience)]
  =>
  (c/insert! (->LineItem ?id :charge 40000)))

(c/defrule tragedy-bonus-amount
  [Play (= :tragedy type) (= ?id id)]
  [Performance (= ?id play-id) (= ?audience audience)]
  [:test (> ?audience 30)]
  =>
  (c/insert! (->LineItem ?id :charge (* 1000 (- ?audience 30)))))

(c/defrule tragedy-volume-credits
  [Play (= :tragedy type) (= ?id id)]
  [Performance (= ?id play-id) (= ?audience audience)]
  =>
  (c/insert! (->LineItem ?id :credit (max (- ?audience 30) 0))))
