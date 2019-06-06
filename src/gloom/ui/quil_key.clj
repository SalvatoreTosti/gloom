(ns gloom.ui.quil-key
  (:use [gloom.entities.player :only [move-player]])
  (:require [quil.core :as q]))

(defn key-pressed [state key-information]
  (case (:key key-information)
    :w (update-in state [:game :world] move-player :n)
    :a (update-in state [:game :world] move-player :w)
    :s (update-in state [:game :world] move-player :s)
    :d (update-in state [:game :world] move-player :e)))
