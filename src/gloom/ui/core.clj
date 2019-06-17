(ns gloom.ui.core
  (:require [quil.core :as q]))

(defrecord UI [kind])

(defn push-ui [game ui]
  (update game :uis #(conj % ui)))

(defn pop-ui [game]
  (update game :uis pop))

(def tile-size 16)
(def screen-size [45 24])

(defn- get-tile [id tile-map]
  (let [result (id tile-map)]
    (if result
      result
      (:821 tile-map))))

(defn- translate-color [RGB-values]
  (case RGB-values
    :white [255 255 255]
    :pink [200 127 180]
    :red [220 20 60]
    RGB-values))

(defn draw-tile
  ([x y tile-map id]
   (let [img (get-tile id tile-map)]
     (when (q/loaded? img)
       (q/image img (* x tile-size) (* y tile-size)))))
  ([x y tile-map id color]
   (let [color (translate-color color)
        r (first color)
        g (second color)
        b (nth color 2)]
  (q/tint r g b)
  (draw-tile x y id tile-map)
  (q/no-tint))))

(defn invert-tile [x y]
  (let [clone (q/create-image tile-size tile-size :rgb)]
    (q/copy
      (q/current-graphics)
      clone
      [(* x tile-size) (* y tile-size) tile-size tile-size]
      [0 0 tile-size tile-size])
    (q/image-filter clone :invert)
    (q/image clone (* x tile-size) (* y tile-size))))

(defn invert-rect [start-x start-y end-x end-y]
  (doseq [x (range start-x end-x)
          y (range start-y end-y)]
    (invert-tile x y)))

(defn clear-screen [tile-map]
  (let [[cols, rows] screen-size
        blank (:0 tile-map)]
    (when (q/loaded? blank)
      (doall (for [x (range cols)
                   y (range rows)]
               (q/image blank (* x tile-size) (* y tile-size)))))))
