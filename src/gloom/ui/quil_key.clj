(ns gloom.ui.quil-key
  (:use [gloom.entities.player :only [move-player]]
        [gloom.entities.core :only [tick]])
  (:require [quil.core :as q]))

(defn tick-entity [world entity]
  (tick entity world))

(defn tick-all [world]
  (reduce tick-entity world (vals (:entities world))))

(defn clear-messages [game]
  (assoc-in game [:world :entities :player :messages] []))

(defn process-tick [game]
;;   (println "in: " (get-in game [:world :entities :player :messages]))
  (let [new-game
  (-> game
      (update-in [:world] tick-all)
;;       (update-in [:world :tick] inc)
      )]
;;       (println (get-in new-game [:world :entities :player :messages]))

   new-game ))
;;               (let [game (update-in game [:world] tick-all)]
;;                     game (clear-messages game)]
;;                 game))

(defn key-pressed [state key-information]
  (->
    (case (:key key-information)
      :w (update-in state [:game :world] move-player :n)
      :a (update-in state [:game :world] move-player :w)
      :s (update-in state [:game :world] move-player :s)
      :d (update-in state [:game :world] move-player :e)
      state)
    (update-in [:game] process-tick)
    (update-in [:game :world :tick] inc)
    ))
