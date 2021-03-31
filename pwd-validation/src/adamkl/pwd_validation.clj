(ns adamkl.pwd-validation)

(defn in?
  [coll x]
  (some? (some #(= x %) coll)))

(def predicates
  {:consecutive  #(= %1 %2)
   :seq-increasing #(= (- %2 %1) 1)
   :seq-decreasing #(= (- %1 %2) 1)})

(defn too-many?
  [max-allowed predicate-key string]
  (let [ints (map int string)
        predicate (predicate-key predicates)]
    (loop [[first-int & rest] ints
           reps 0]
      (let [too-many (>= reps max-allowed)
            second-int (first rest)]
        (if (or too-many
                (not second-int))
          too-many
          (recur rest
                 (if (predicate first-int second-int)
                   (inc reps)
                   0)))))))

; Rich comment for testing
(comment

  (def pw-good "p@ssword")
  (def pw-bad "passsw0rd123cba")
  (def verboten-words ["password"
                       "passw0rd"
                       "passsw0rd123cba"])

  (in? verboten-words pw-good)
  (too-many? 2 :consecutive pw-good)
  (too-many? 2 :seq-increasing pw-good)
  (too-many? 2 :seq-decreasing pw-good)

  (in? verboten-words pw-bad)
  (too-many? 2 :consecutive pw-bad)
  (too-many? 2 :seq-increasing pw-bad)
  (too-many? 2 :seq-decreasing pw-bad)

  (comment))