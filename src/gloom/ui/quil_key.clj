(ns gloom.ui.quil-key
  (:require [quil.core :as q]))

(defn key-pressed [state key-information]
  (let [state (assoc state :pressed true)]
  (case (:key key-information)
    :w (update state :y dec)
    :a (update state :x dec)
    :s (update state :y inc)
    :d (update state :x inc)
    state)))
