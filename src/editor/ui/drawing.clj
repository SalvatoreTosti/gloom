(ns editor.ui.drawing
  (:use
    [editor.ui.core :only [get-id]]
    [editor.ui.views.grid :only [make-grid-view]]
    [editor.ui.views.canvas :only [make-canvas-view]]
    [editor.ui.views.entity-builder :only [make-entity-builder-view]]
    [editor.ui.views.dialogs.text-dialog :only [make-text-dialog]]
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

(defn add-dialog [state dialog]
  (let [dialogs (get-in state [:editor :dialogs])
        dialogs (or dialogs '())]
    (->> dialog
         (conj dialogs)
         (assoc-in state [:editor :dialogs]))))

(defn make-editor [state]
  (q/frame-rate 30)
  (let [palette-view (make-grid-view
                       {:position [0 0]
                        :width 10
                        :height 24
                        :outline-id :119
                        :cursor-id :787}
                       state)
        canvas-view (make-canvas-view
                      {:position [9 0]
                       :width 36
                       :height 12
                       :outline-id :119
                       :cursor-id :787
                       :palette-view palette-view}
                      state)
        entity-builder (make-entity-builder-view
                         {:position [9 12]
                          :width 36
                          :height 12
                          :outline-id :119
                          :cursor-id :787}
                         state)
        text-dialog (make-text-dialog
                      {:position [9 12]
                       :width 20
                       :height 6
                       :outline-id :119
                       :cursor-id :787
                       :callback-path [:editor :a-test]}
                      state)]
    (-> state
        (add-view palette-view)
        (add-view canvas-view)
        (add-view entity-builder)
        ;;(add-dialog text-dialog) 
        )))

(defn draw-editor [state]
  (q/no-cursor)
  (clear-screen  (:screen-size state)  (:tile-map state))
  (doseq [view (vals (get-in state [:editor :views]))]
    ((:draw-fn view) view state))
  (doseq [view (get-in state [:editor :dialogs])]
    ((:draw-fn view) view state))
  (draw-cursor state))
