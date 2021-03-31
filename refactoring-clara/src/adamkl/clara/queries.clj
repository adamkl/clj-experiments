(ns adamkl.clara.queries
  (:require [clara.rules :as c]
            [adamkl.clara.facts])
  (:import [adamkl.clara.facts LineItem Total]))

(declare get-line-items)
(c/defquery get-line-items
  []
  [?line-item <- LineItem])

(declare get-totals)
(c/defquery get-totals
  []
  [?total <- Total])