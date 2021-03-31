(ns adamkl.clara.facts)

;; ---- logic ----
(defrecord Invoice [id client-name performances])
(defrecord Performance [play-id audience])
(defrecord Play [id type name])
(defrecord LineItem [id type value])
(defrecord Total [id type value])



