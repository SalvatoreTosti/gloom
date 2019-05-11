(ns clj-boc.input
  (:use [clj-boc.world :only [random-world smooth-world]]
        [clj-boc.UIcore :only [->UI]])
  (:require [lanterna.screen :as s]))

(defn move [[x y] [dx dy]]
  [(+ x dx) (+ y dy)])

(defmulti process-input
  (fn [game input]
    (:kind (last (:uis game)))))

(defmethod process-input :start [game input]
  (-> game
      (assoc :world (random-world))
      (assoc :uis [(->UI :play)])))

(defmethod process-input :play [game input]
  (case input
    :enter (assoc game :uis [(->UI :win)])
    :backspace (assoc game :uis [(->UI :lose)])
    \q (assoc game :uis [])
    \m (assoc game :world (smooth-world (:world game)))

    \w (update-in game [:location] move [0 -1])
    \a (update-in game [:location] move [-1 0])
    \s (update-in game [:location] move [0 1])
    \d (update-in game [:location] move [1 0])

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
