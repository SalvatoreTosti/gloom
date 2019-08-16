(ns main.ui
   (:use [gloom.ui.quil-setup :only [reset-state-game get-tiles]]
         [gloom.ui.quil-drawing :only [draw-game]]
         [gloom.ui.quil-key :only [process-input-game]]
         [gloom.ui.core :only [tile-size clear-screen]]
         [gloom.ui.quil-text :only [draw-text draw-text-centered]]
         [editor.ui.drawing :only [draw-editor make-editor]]
         [editor.ui.input :only [process-input-editor]]
         )
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
    (clear-screen  (:screen-size state)  (:tile-map state))

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
  (draw-editor state))

(defmulti process-input
  (fn [state key-information]
    (:mode state)))

(defmethod process-input :start [state key-information]
  (case (:key key-information)
    :p (-> state
           (reset-state-game)
           (assoc :mode :play))
    :e (-> state
           (make-editor)
           (assoc :mode :edit))
    state))


(defmethod process-input :play [state key-information]
  (process-input-game state key-information))

(defmethod process-input :edit [state key-information]
  (process-input-editor state key-information))

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
