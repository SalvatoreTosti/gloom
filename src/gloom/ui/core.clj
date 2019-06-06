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

(defn draw-tile
  ([x y image]
   (when (q/loaded? image)
     (q/image image (* x tile-size) (* y tile-size))))
  ([x y tile-map id]
     (draw-tile x y (id tile-map))))
