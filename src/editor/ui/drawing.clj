(ns editor.ui.drawing
   (:use
     [editor.ui.core :only [get-id]]
     [editor.ui.input :only [on-click]]
     [editor.ui.views :only [make-grid-view draw-list-view]]
     [gloom.ui.core :only [clear-screen draw-tile]]
     [gloom.ui.quil-text :only [draw-text draw-text-centered]]
     [gloom.ui.core :only [tile-size]]
     )
  (:require [quil.core :as q]
            [quil.middleware :as m]))

(defn- round-down [number base]
  (* base (int (Math/floor (/ number base)))))

(defn- draw-cursor [state]
  (let [current-x (/ (round-down (q/mouse-x) tile-size) tile-size)
        current-y (/ (round-down (q/mouse-y) tile-size) tile-size)]
  (draw-tile current-x current-y (:tile-map state) :787)))

(defn make-editor [state]
  (assoc-in
    state
    [:editor :views]
    [(make-grid-view [0 0] 10 24 :119 :787)
     (make-grid-view [9 0] 36 12 :119 :787)]
    ))

(defn draw-editor [state]
  (clear-screen  (:screen-size state)  (:tile-map state))
  (draw-text-centered (dec (int (/ (second (:screen-size state)) 2)))
                      (first (:screen-size state))
                      (:tile-map state)
                      "edit mode")
  (doseq [view (get-in state [:editor :views])]
      ((:draw-fn view) view state))
  (draw-cursor state))
