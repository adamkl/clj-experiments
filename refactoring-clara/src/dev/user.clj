(ns user
  (:require [clara.tools.inspect :as tools]
            [clara.rules :as c]
            [adamkl.clara.core :as core]
            [adamkl.clara.facts :as facts]
            [adamkl.clara.queries :as queries]
            [adamkl.clara.rules.base]
            [adamkl.clara.rules.comedy]
            [adamkl.clara.rules.tragedy])
  (:import [adamkl.clara.facts Play Performance]))


(comment
  (def plays (map #(facts/map->Play %) [{:id :hamlet
                                         :type :comedy
                                         :name "Hamlet"}
                                        {:id :as-like
                                         :type :tragedy
                                         :name "As You Like It"}
                                        {:id :othello
                                         :type :tragedy
                                         :name "Othello"}]))
  (instance? Play (second plays))

  (def invoice (facts/->Invoice :big-co
                                "BigCo"
                                (map #(facts/map->Performance %)
                                     [{:play-id :hamlet :audience 55}
                                      {:play-id :as-like :audience 35}
                                      {:play-id :othello :audience 30}])))

  (instance? Performance (-> invoice :performances first))

  (-> (core/gen-statement plays invoice)
      (print))

  (def base-session (-> (c/mk-session 'adamkl.clara.queries
                                      'adamkl.clara.rules.base
                                      'adamkl.clara.rules.comedy
                                      'adamkl.clara.rules.tragedy
                                      :cache false)
                        (c/insert-all plays)
                        (c/insert-all (:performances invoice))))

  (-> base-session
      (c/fire-rules)
      (c/query queries/get-line-items))

  (comment))