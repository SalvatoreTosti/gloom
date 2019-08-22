(ns editor.ui.drawing
   (:use
     [editor.ui.core :only [get-id]]
     [editor.ui.views.grid :only [make-grid-view]]
     [editor.ui.views.canvas :only [make-canvas-view]]
     [gloom.ui.core :only [clear-screen draw-tile tile-size]]
     [gloom.ui.quil-text :only [draw-text draw-text-centered]]
     )
  (:require [quil.core :as q]
            [quil.middleware :as m]))

(defn- round-down [number base]
  (* base (int (Math/floor (/ number base)))))

(defn- draw-cursor [state]
  (let [current-x (/ (round-down (q/mouse-x) tile-size) tile-size)
        current-x (inc current-x)
        current-y (/ (round-down (q/mouse-y) tile-size) tile-size)
        current-y (inc current-y)]
  (draw-tile current-x current-y (:tile-map state) :723)))

(defn- add-view [state view]
  (assoc-in state [:editor :views (:id view)] view))

(defn make-editor [state]
    (q/frame-rate 30)
  (let [palette-view (make-grid-view [0 0] 10 24 :119 :787 state)
        canvas-view (make-canvas-view [9 0] 36 12 :119 :787 palette-view state)]
    (-> state
        (add-view palette-view)
        (add-view canvas-view))))

(defn draw-editor [state]
  (q/no-cursor)
  (clear-screen  (:screen-size state)  (:tile-map state))
  (draw-text-centered (dec (int (/ (second (:screen-size state)) 2)))
                      (first (:screen-size state))
                      (:tile-map state)
                      "edit mode")
  (doseq [view (vals (get-in state [:editor :views]))]
      ((:draw-fn view) view state))
  (draw-cursor state))
