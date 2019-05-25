(ns gloom.ui.core)

(defrecord UI [kind])

(defn push-ui [game ui]
  (update game :uis #(conj % ui)))

(defn pop-ui [game]
  (update game :uis pop))
