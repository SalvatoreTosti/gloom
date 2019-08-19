(ns editor.ui.core
  (:use
    [gloom.ui.core :only [tile-size]]
    )
   (:require [quil.core :as q]))

(def ids (ref 0))

(defn get-id []
  (dosync
    (let [id @ids]
      (alter ids inc)
      id)))

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

(defn- round-down [number base]
  (* base (int (Math/floor (/ number base)))))

(defn mouse->grid [view]
  (let [current-x (/ (round-down (q/mouse-x) tile-size) tile-size)
        current-y (/ (round-down (q/mouse-y) tile-size) tile-size)]
    [current-x current-y]))
