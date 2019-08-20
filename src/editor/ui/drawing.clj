(ns editor.ui.drawing
   (:use
     [editor.ui.core :only [get-id]]
     [editor.ui.views.grid :only [make-grid-view]]
     [gloom.ui.core :only [clear-screen draw-tile tile-size]]
     [gloom.ui.quil-text :only [draw-text draw-text-centered]]
     )
  (:require [quil.core :as q]
            [quil.middleware :as m]))

(defn- round-down [number base]
  (* base (int (Math/floor (/ number base)))))

(defn- draw-cursor [state]
  (let [current-x (/ (round-down (q/mouse-x) tile-size) tile-size)
        current-y (/ (round-down (q/mouse-y) tile-size) tile-size)]
  (draw-tile current-x current-y (:tile-map state) :787)))

(defn- add-view [state view]
  (assoc-in state [:editor :views (:id view)] view))

(defn make-editor [state]
 (-> state
     (add-view (make-grid-view [0 0] 10 24 :119 :787 state))
     (add-view (make-grid-view [9 0] 36 12 :119 :787 state))))

(defn draw-editor [state]
  (clear-screen  (:screen-size state)  (:tile-map state))
  (draw-text-centered (dec (int (/ (second (:screen-size state)) 2)))
                      (first (:screen-size state))
                      (:tile-map state)
                      "edit mode")
  (doseq [view (vals (get-in state [:editor :views]))]
      ((:draw-fn view) view state))
  (draw-cursor state))
