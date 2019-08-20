(ns editor.ui.views.core
  (:use
    [gloom.ui.core :only [tile-size]]
    [editor.ui.core :only [get-id]])
  (:require [quil.core :as q]))


(defn- coordinates-between? [[x y] [start-x start-y] [end-x end-y]]
  (and
    (>= x start-x)
    (<= x end-x)
    (>= y start-y)
    (<= y end-y)))

(defn coordinates-in-view? [coordinates view]
  (coordinates-between?
    coordinates
    (get-in view [:pixel-coordinates :start])
    (get-in view [:pixel-coordinates :end])))

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

(defn- round-down [number base]
  (* base (int (Math/floor (/ number base)))))

(defn mouse->grid [view]
  (let [current-x (/ (round-down (q/mouse-x) tile-size) tile-size)
        current-y (/ (round-down (q/mouse-y) tile-size) tile-size)]
    [current-x current-y]))
