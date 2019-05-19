(ns clj-boc.ui.input
  (:use [clj-boc.world :only [random-world smooth-world find-empty-tile]]
        [clj-boc.ui.core :only [->UI]]
        [clj-boc.entities.player :only [make-player move-player]]
        [clj-boc.entities.lichen :only [make-lichen]]
        [clj-boc.entities.bunny :only [make-bunny]]
        [clj-boc.entities.apple :only [make-apple]])
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
  (let [world (assoc-in world [:entities :player]
                        (make-player world))]
    (-> world
        (add-creatures make-lichen 30)
        (add-creatures make-bunny 20)
        (add-creatures make-apple 30)
        )
    ))

(defn reset-game [game]
  (let [fresh-world (random-world)]
    (-> game
        (assoc :world fresh-world)
        (update-in [:world] populate-world)
        (assoc :uis [(->UI :play)]))))

(defmulti process-input
  (fn [game input]
    (:kind (last (:uis game)))))

(defmethod process-input :start [game input]
  (reset-game game))

(defmethod process-input :play [game input]
  (case input
    :enter (assoc game :uis [(->UI :win)])
    :backspace (assoc game :uis [(->UI :lose)])
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

(defn get-input [game screen]
  (assoc game :input (s/get-key-blocking screen)))
