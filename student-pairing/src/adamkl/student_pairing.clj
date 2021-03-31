(ns adamkl.student-pairing
  (:require [clojure.string :as str]
            [clojure.pprint :refer [pprint]]))

;; see https://en.wikipedia.org/wiki/Graph_factorization#Complete_graphs
(defn calc-perpendicular [num-pairs curr-index rest]
  (->> (range 1 num-pairs)
       (map (fn [x]
              [(nth rest (mod (- curr-index x) (count rest)))
               (nth rest (mod (+ curr-index x) (count rest)))]))))

(defn get-pairs [num-pairs first rest]
  (->> rest
       (map-indexed
        (fn [i x]
          (cons [first x]
                (calc-perpendicular num-pairs i rest))))))

(defn factor [list]
  (get-pairs (/ (count list) 2)
             (first list)
             (rest list)))

(defn read-file [path]
  (-> path
      (slurp)
      (str/split-lines)))

(defn format-day [curr-index pairings]
  (let [day (str "\nDay " (+ curr-index 1) "\n")
        formatted-pairs (->> pairings
                             (map (fn [pair]
                                    (if
                                     (= (count (set pair)) 1)
                                      (str "Floater: " (nth pair 0) "\n")
                                      (str (str/join ", " pair) "\n"))))
                             (apply str))]
    (str day formatted-pairs)))

(defn format-output [results]
  (map-indexed format-day results))

(def file-path "data/students.txt")
(def out-path "data/output.txt")

(defn -main []
  (->> (read-file file-path)
       (factor)
       (format-output)
       (apply str)
       (spit out-path)))

(-main)
