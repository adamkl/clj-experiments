(ns adamkl.refactoring
  (:require [clojure.string :as s]))

; /*
;   Exercise taken from : Refactoring - Improving the Design of Existing Code by Martin Fowler

;   Imagine a company of a theatrical players who go out to various events performing
;   plays.  Typically, a customer will request a few plays and the company charges
;   them based on the size of the audience and the kind of play they perform.  There
;   are currently two kinds of plays that the company performs: tragedies and
;   comedies.  As well as providing a bill for the performance, the company gives its
;   customers "volume credits" which they can use for discounts on future performances --
;   think of it as a customer loyalty mechanism.
; */

(defn usd [amount]
  (let [formatter (-> (java.util.Locale. "en" "US")
                      (java.text.NumberFormat/getCurrencyInstance))]
    (.format formatter amount)))

(defn keyword-sum [kw col]
  (->> (map kw col)
       (reduce +)))

(defn calc-total-amount [performances]
  (keyword-sum :amount performances))

(defn calc-total-vol-credits [performances]
  (keyword-sum :vol-credits performances))

(defn play-for [perf plays]
  (plays (perf :playID)))

(defmulti calc-perf-amount :type)

(defmethod calc-perf-amount :default [{type :type} _]
  (throw (Exception. (str "Invalid play type: " type))))

(defmethod calc-perf-amount :comedy [_ {audience :audience}]
  (let [base 30000
        per-seat (* 300 audience)
        bonus (+ 10000 (* 500 (- audience 20)))]
    (if (<= audience 20)
      (+ base per-seat)
      (+ base per-seat bonus))))


(defmethod calc-perf-amount :tragedy [_ {audience :audience}]
  (let [base 40000
        bonus (* 1000 (- audience 30))]
    (if (<= audience 30)
      (+ base)
      (+ base bonus))))

(defmulti calc-perf-volume :type)
(defmethod calc-perf-volume :default [_ {audience :audience}]
  (max (- audience 30) 0))

(defmethod calc-perf-volume :comedy [_ {audience :audience :as perf}]
  (+ (calc-perf-volume {:type :default} perf) (int (Math/floor (/ audience 5)))))

(defn enrich-perf [perf play]
  (merge perf {:amount (calc-perf-amount play perf)
               :vol-credits (calc-perf-volume play perf)
               :play play}))

(defn create-statement-data [invoice, plays]
  (let [performances (map #(enrich-perf % (play-for % plays)) (:performances invoice))]
    {:customer (:customer invoice)
     :performances performances
     :total-amount (calc-total-amount performances)
     :total-vol-credits (calc-total-vol-credits performances)}))

(defn statement [invoice plays]
  (let [{:keys [customer
                performances
                total-amount
                total-vol-credits]} (create-statement-data invoice plays)]
    (s/join
     "\n"
     (flatten
      [(str "Statement for " customer)
       (mapv
        #(str
          " "
          (-> % :play :name)
          ": "
          (usd (/ (-> % :amount) 100))
          " (" (-> % :audience) " seats)")
        performances)
       (str "Amount owed is " (usd (/ total-amount 100)))
       (str "You earned " total-vol-credits " credits")]))))
