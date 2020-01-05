(ns editor.ui.views.core
  (:use
    [gloom.ui.core :only [tile-size draw-tile]]
    [editor.ui.core :only [get-id]]
    [gloom.ui.quil-text :only [draw-text]])
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

(defn draw-view-outline [view state]
  (let [[start-x start-y] (:position view)
        end-x (dec (+ start-x (:width view)))
        end-y (dec (+ start-y (:height view)))]
    (doseq [x (range start-x end-x)]
            (draw-tile x start-y (:tile-map state) (:outline-id view)))
    (doseq [x (range start-x (inc end-x))]
            (draw-tile x end-y (:tile-map state) (:outline-id view)))
    (doseq [y (range start-y end-y)]
      (draw-tile start-x y (:tile-map state) (:outline-id view))
      (draw-tile end-x y (:tile-map state) (:outline-id view)))))

(defn draw-text-relative [x y view state message]
  (let [[pos-x pos-y] (:position view)
         start-x (+ pos-x x)
         start-y (+ pos-y y)]
    (draw-text start-x start-y (:tile-map state) message)))

(defn make-view
  [{:keys
    [position
     width
     height
     outline-id
     cursor-id
     draw-fn
     on-click-fn
     on-input-fn]}]
  (let
    [[x y] position
     start-x (inc x)
     start-y (inc y)
     end-x (dec (dec (+ start-x width)))
     end-y (dec (dec (+ start-y height)))]
    {:id (get-id)
     :position position
     :width width
     :height height
     :outline-id outline-id
     :cursor-id cursor-id
     :draw-fn draw-fn
     :on-click-fn on-click-fn
     :on-input-fn on-input-fn
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
     :start [start-x start-y]
     :end [end-x end-y]
     }))

(defn- round-down [number base]
  (* base (int (Math/floor (/ number base)))))

(defn mouse->grid
  ([view]
   (mouse->grid [(q/mouse-x) (q/mouse-y)] tile-size view))
  ([[x y] tile-size view]
   [(/ (round-down x tile-size) tile-size)
   (/ (round-down y tile-size) tile-size)]))
