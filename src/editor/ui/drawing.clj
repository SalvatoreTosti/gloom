(ns editor.ui.drawing
   (:use
     [editor.ui.core :only [get-id]]
     [editor.ui.input :only [on-click]]
     [gloom.ui.core :only [clear-screen draw-tile]]
     [gloom.ui.quil-text :only [draw-text draw-text-centered]]
     [gloom.ui.core :only [tile-size]]
     )
  (:require [quil.core :as q]
            [quil.middleware :as m]))

(defn make-view [[x y] width height outline-id cursor-id]
  {:id (get-id)
   :position [x y]
   :width width
   :height height
   :outline-id outline-id
   :cursor-id cursor-id
   :pixel-coordinates
     {
       :start [
                (* tile-size x)
                (* tile-size y)
                ]
       :end [
              (* tile-size (+ x width))
              (* tile-size (+ y height))
              ]
     }
   })

(defn draw-view [view state]
  (let [start-x (first (:position view))
        start-y (second (:position view))
        end-x (dec (+ start-x (:width view)))
        end-y (dec (+ start-y (:height view)))]
    (doseq [x (range start-x end-x)]
            (draw-tile x start-y (:tile-map state) (:outline-id view)))
    (doseq [x (range start-x (inc end-x))]
            (draw-tile x end-y (:tile-map state) (:outline-id view)))
    (doseq [y (range start-y end-y)]
      (draw-tile start-x y (:tile-map state) (:outline-id view))
      (draw-tile end-x y (:tile-map state) (:outline-id view))
      )))

(defn draw-rect [[start-x start-y] width height state]
  (let [end-x (dec (+ start-x width))
        end-y (dec (+ start-y height))]
    (doseq [x (range start-x end-x)]
            (draw-tile x start-y (:tile-map state) :118))
    (doseq [x (range start-x (inc end-x))]
            (draw-tile x end-y (:tile-map state) :118))
    (doseq [y (range start-y end-y)]
      (draw-tile start-x y (:tile-map state) :118)
      (draw-tile end-x y (:tile-map state) :118)
      )))

(defn round-down [number base]
  (* base (int (Math/floor (/ number base)))))

(defn draw-cursor [state]
  (let [current-x (/ (round-down (q/mouse-x) tile-size) tile-size)
        current-y (/ (round-down (q/mouse-y) tile-size) tile-size)]
  (draw-tile current-x current-y (:tile-map state) :787)))

(defn make-editor [state]
  (assoc-in
    state
    [:editor :views]
    [(make-view [0 0] 10 24 :119 :787)
     (make-view [9 0] 10 24 :119 :787)]
    ))

(defn draw-editor [state]
  (when (q/mouse-button)
    (println (on-click state)))
  (clear-screen  (:screen-size state)  (:tile-map state))
  (draw-text-centered (dec (int (/ (second (:screen-size state)) 2)))
                      (first (:screen-size state))
                      (:tile-map state)
                      "edit mode")
  (doseq [view (get-in state [:editor :views])]
    (draw-view view state))
  (draw-cursor state))
