(ns adamkl.clara.core
  (:require [clara.rules :as c]
            [adamkl.clara.queries :refer [get-totals]]
            [adamkl.clara.rules.base]
            [adamkl.clara.rules.comedy]
            [adamkl.clara.rules.tragedy]))

;; ---- logic ----
(defn perform-calc [plays performances]
  (-> (c/mk-session 'adamkl.clara.queries
                    'adamkl.clara.rules.base
                    'adamkl.clara.rules.comedy
                    'adamkl.clara.rules.tragedy)
      (c/insert-all plays)
      (c/insert-all performances)
      (c/fire-rules)))

;; ---- formatting ----
(defn usd [amount]
  (let [formatter (-> (java.util.Locale. "en" "US")
                      (java.text.NumberFormat/getCurrencyInstance))]
    (.format formatter (/ amount 100))))

(defn header-strings [client]
  ["\n"
   (str "Statment for customer: " client "\n")])

(defn sub-total-strings [sub-totals plays performances]
  (->> sub-totals
       (map (fn [[id play-totals]]
              (let [play (some #(when (= id (:id %)) (:name %)) plays)
                    audience (some #(when (= id (:play-id %)) (:audience %)) performances)
                    charge (some #(when (= :charge (:type %)) (:value %)) play-totals)]
                (str " " play ": " (usd charge) " (" audience " seats)\n"))))))

(defn grand-total-strings [grand-totals]
  (let [charge (some #(when (= :charge (:type %)) (:value %)) grand-totals)
        credit (some #(when (= :credit (:type %)) (:value %)) grand-totals)]
    [(str "Amount owed is " (usd charge) "\n")
     (str "You earned " credit " credits\n")]))

(defn gen-statement [plays invoice]
  (let [{:keys [client-name performances]} invoice
        session (perform-calc plays performances)
        totals (->> (c/query session get-totals)
                    (map :?total)
                    (group-by :id))
        grand-totals (:grand totals)
        sub-totals (dissoc totals :grand)]
    (->> [(header-strings client-name)
          (sub-total-strings sub-totals plays performances)
          (grand-total-strings grand-totals)]
         (flatten)
         (apply str))))

