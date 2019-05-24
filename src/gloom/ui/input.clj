(ns gloom.ui.input
  (:use [gloom.world :only [random-world smooth-world find-empty-tile]]
        [gloom.ui.core :only [->UI]]
        [gloom.entities.player :only [make-player move-player]]
        [gloom.entities.lichen :only [make-lichen]]
        [gloom.entities.bunny :only [make-bunny]]
        [gloom.entities.apple :only [make-apple]]
        [gloom.ui.entities.menu :only [make-menu]]
        [gloom.ui.entities.aspects.selection :only [up down select]])
  (:require [lanterna.screen :as s]))

(defn move [[x y] [dx dy]]
  [(+ x dx) (+ y dy)])

(defn add-creature [world make-creature]
  (let [creature (make-creature (find-empty-tile world))]
    (assoc-in world [:entities (:id creature)] creature)))

(defn add-creatures [world make-creature n]
  (nth (iterate #(add-creature % make-creature)
                world)
       n))

(defn populate-world [world]
  (let [world (assoc-in world [:entities :player] (make-player world))]
    (-> world
        (add-creatures make-lichen 30)
        (add-creatures make-bunny 20)
        (add-creatures make-apple 30))))

(defn reset-game [game]
  (let [fresh-world (random-world)]
    (-> game
        (assoc :world fresh-world)
        (update-in [:world] populate-world)
        (assoc :uis [(->UI :play)]))))

(defn skip-tick [game]
  (assoc game :skip-tick true))

(defmulti process-input
  (fn [game input]
    (:kind (last (:uis game)))))

(defmethod process-input :start [game input]
  (reset-game game))

(defn initalize-selection [game]
  (let [ui (first (:uis game))
        new-ui (assoc ui :selection 0)]
    (assoc game :uis [new-ui])))

(defn make-inventory-menu [game]
    (let [inv (get-in game [:world :entities :player :inventory])
          items (vals inv)
          items (map :name items)]
  (make-menu ["Inventory"] items)))

(defmethod process-input :play [game input]
  (case input
    :enter (assoc game :uis [(->UI :win)])
    :backspace (assoc game :uis [(->UI :lose)])
    \n (-> game
           (assoc :uis [(make-inventory-menu game)])
           (skip-tick))
    \x (-> game
           (assoc :uis [(make-menu ["Spells"] ["a" "b" "c"])])
           (skip-tick))

    \q (assoc game :uis [])

    \w (update-in game [:world] move-player :n)
    \a (update-in game [:world] move-player :w)
    \s (update-in game [:world] move-player :s)
    \d (update-in game [:world] move-player :e)

    game))

(defmethod process-input :win [game input]
  (if (= input :escape)
    (assoc game :uis [])
    (assoc game :uis [(->UI :start)])))

(defmethod process-input :lose [game input]
  (if (= input :escape)
    (assoc game :uis [])
    (assoc game :uis [(->UI :start)])))

(defn selection-up [game]
  (let [current-UI (first (:uis game))
        new-UI (update current-UI :selection dec)]
    (assoc game :uis [new-UI])))

(defn selection-down [game]
  (let [current-UI (first (:uis game))
        new-UI (update current-UI :selection inc)]
    (assoc game :uis [new-UI])))

(defmethod process-input :menu [game input]
  (let [game (skip-tick game)
        ui (first (:uis game))]
    (case input
      :escape (assoc game :uis [(->UI :play)])
      :enter (do
               (println (select ui game))
               game)

      \w (up ui game)
      \s (down ui game)

      game)))

(defn get-input [game screen]
  (assoc game :input (s/get-key-blocking screen)))
