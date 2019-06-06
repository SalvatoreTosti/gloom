(ns gloom.ui.core
  (:require [quil.core :as q]))

(defrecord UI [kind])

(defn push-ui [game ui]
  (update game :uis #(conj % ui)))

(defn pop-ui [game]
  (update game :uis pop))

(def tile-size 16)

;; (defn tile-lookup [id tile-map]
;;   (id tile-map))

;; (def tile-lookup-mem (memoize tile-lookup))


(defn- get-tile [id tile-map]
  (let [result (id tile-map)]
    (if result
      result
      (:821 tile-map))))

(defn- translate-color [RGB-values]
  (case RGB-values
    :white [255 255 255]
    :pink [200 127 180]
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
