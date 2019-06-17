(ns gloom.ui.quil-key
  (:use [gloom.entities.player :only [move-player]]
        [gloom.ui.core :only [push-ui pop-ui]]
        [gloom.ui.entities.menu :only [make-menu]]
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

(defmulti process-input
  (fn [state key-information]
    (let [ui (last (get-in state [:game :uis]))]
    (:kind ui))))

(defn tick-state [state]
  (-> state
      (update-in [:game] process-tick)
      (update-in [:game :world :tick] inc)))

(defmethod process-input :play [state key-information]
  (case (:key key-information)
    :w (-> state
           (update-in [:game :world] move-player :n)
           tick-state)
    :a (-> state
           (update-in [:game :world] move-player :w)
           tick-state)
    :s (-> state
           (update-in [:game :world] move-player :s)
           tick-state)
    :d (-> state
           (update-in [:game :world] move-player :e)
           tick-state)
    :q (-> state
           (update-in [:game] push-ui (make-menu "Spells" {:a {:name "a"}, :b {:name "b"}, :c {:name "c"}} [:name])))
    state))

(defmethod process-input :menu [state key-information]
  (->
    (case (:key key-information)
      :q (update-in state [:game] pop-ui)
      state)))
;;     (update-in [:game] process-tick)
;;     (update-in [:game :world :tick] inc)

(defn key-pressed [state key-information]
  (process-input state key-information)
  )
;;   (process-input state key-information))
;;   (->
;;     (case (:key key-information)
;;       :w (update-in state [:game :world] move-player :n)
;;       :a (update-in state [:game :world] move-player :w)
;;       :s (update-in state [:game :world] move-player :s)
;;       :d (update-in state [:game :world] move-player :e)
;;       :q (do
;;            (println (get-in state [:game :uis]))
;;            (update-in state [:game] push-ui (make-menu "Spellz" {:a {:name "a"}, :b {:name "b"}, :c {:name "c"}} [:name])))
;;       state)
;;     (update-in [:game] process-tick)
;;     (update-in [:game :world :tick] inc)

