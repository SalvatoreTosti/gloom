(ns editor.ui.drawing
   (:use [gloom.ui.core :only [tile-size, clear-screen]]
         [gloom.ui.quil-text :only [draw-text draw-text-centered]])
  (:require [quil.core :as q]
            [quil.middleware :as m]))

(defn draw-editor [state]
  (clear-screen  (:screen-size state)  (:tile-map state))
  (draw-text-centered (dec (int (/ (second (:screen-size state)) 2)))
                      (first (:screen-size state))
                      (:tile-map state)
                      "EDIT mode"))
