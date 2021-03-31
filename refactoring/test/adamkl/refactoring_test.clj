(ns adamkl.refactoring-test
  (:require [clojure.string :as s]
            [clojure.test :as t]
            [adamkl.refactoring :refer [statement]]))

(def invoices [{:customer "BigCo"
                :performances [{:playID :hamlet
                                :audience 55}
                               {:playID :as-like
                                :audience 35}
                               {:playID :othello
                                :audience 40}]}
               {:customer "MU-SE"
                :performances [{:playID :hamlet
                                :audience 30}
                               {:playID :as-like
                                :audience 20}]}
               {:customer "The Historians"
                :performances [{:playID :kingjohn
                                :audience 30}]}])

(def plays {:hamlet {:name "Hamlet"
                     :type :tragedy}
            :as-like {:name "As You Like It"
                      :type :comedy}
            :kingjohn {:name "King John"
                       :type :history}
            :othello {:name "Othello"
                      :type :tragedy}})


; /*
;   Imagine a company of a theatrical players who go out to various events performing
;   plays.  Typically, a customer will request a few plays and the company charges
;   them based on the size of the audience and the kind of play they perform.  There
;   are currently two kinds of plays that the company performs: tragedies and
;   comedies.  As well as providing a bill for the performance, the company gives its
;   customers "volume credits" which they can use for discounts on future performances --
;   think of it as a customer loyalty mechanism.
; */

(def BigCo-statement (s/join
                      "\n"
                      ["Statement for BigCo"
                       " Hamlet: $650.00 (55 seats)"
                       " As You Like It: $580.00 (35 seats)"
                       " Othello: $500.00 (40 seats)"
                       "Amount owed is $1,730.00"
                       "You earned 47 credits"]))
(t/deftest produces-correct-statement-for-BigCo
  (t/is (= BigCo-statement (statement (nth invoices 0) plays))))

(def MU-SE-statement (s/join
                      "\n"
                      ["Statement for MU-SE"
                       " Hamlet: $400.00 (30 seats)"
                       " As You Like It: $360.00 (20 seats)"
                       "Amount owed is $760.00"
                       "You earned 4 credits"]))
(t/deftest produces-correct-statement-for-MU-SE
  (t/is (= MU-SE-statement (statement (nth invoices 1) plays))))

(t/deftest throws-error-for-The-Historians
  (t/is (thrown-with-msg? Exception #"Invalid play type: :history"
                          (statement (nth invoices 2) plays))))

(comment (t/run-tests))
