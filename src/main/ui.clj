(ns main.ui
   (:use [gloom.ui.quil-setup :only [reset-state-game get-tiles]]
         [gloom.ui.quil-drawing :only [draw-game]]
         [gloom.ui.quil-key :only [process-input-game]]
         [gloom.ui.core :only [tile-size]]
         [gloom.ui.quil-text :only [draw-text draw-text-centered]])
  (:require [quil.core :as q]
            [quil.middleware :as m]))

(defn setup []
  (q/background 0)
  (q/frame-rate 15)

  (let [base-image (q/load-image "resources/monochrome.png")]
    (while (not (q/loaded? base-image))
      nil)
    {:screen-size [45 24]
     :mode :start
     :img base-image
     :tile-map (get-tiles base-image 32 32)}))

(defmulti draw-main
  (fn [state]
    (:mode state)))

(defmethod draw-main :start [state]
  (let [center-y (dec (int (/ (second (:screen-size state)) 2)))]
    (draw-text-centered center-y
                        (first (:screen-size state))
                        (:tile-map state)
                        "gloom")
    (draw-text-centered (inc center-y)
                        (first (:screen-size state))
                        (:tile-map state)
                        "Press a key!")
    (draw-text-centered (+ 2 center-y)
                        (first (:screen-size state))
                        (:tile-map state)
                        "p : launches the game")
    (draw-text-centered (+ 3 center-y)
                        (first (:screen-size state))
                        (:tile-map state)
                        "e : launches the editor")))

(defmethod draw-main :play [state]
  (draw-game state))

(defmethod draw-main :edit [state]
  (draw-text-centered (dec (int (/ (second (:screen-size state)) 2)))
                      (first (:screen-size state))
                      (:tile-map state)
                      "edit"))

(defmulti process-input
  (fn [state key-information]
    (:mode state)))

(defmethod process-input :start [state key-information]
  (case (:key key-information)
    :p (-> state
           (reset-state-game)
           (assoc :mode :play))
    :e (-> state
           (assoc :mode :edit))
    state))


(defmethod process-input :play [state key-information]
  (process-input-game state key-information))

(defn make-sketch []
  (q/defsketch gloom-sketch
    :title "gloom"
    :size (let [screen-size [45 24]]
            [(* (first  screen-size) tile-size)
             (* (second screen-size) tile-size)])
    :setup setup
    :draw draw-main
    :key-pressed process-input
    :middleware [m/fun-mode]))
