(ns editor.ui.views.entity-builder
  (:use
    [gloom.ui.core :only [draw-tile]]
    [editor.ui.views.core :only [make-view mouse->grid draw-text-relative]]))

(defn- draw-view [view state]
  (let [start-x (first (:position view))
        start-y (second (:position view))
        end-x (dec (+ start-x (:width view)))
        end-y (dec (+ start-y (:height view)))]
    (doseq [x (range start-x end-x)]
            (draw-tile x start-y (:tile-map state) (:outline-id view)))
    (doseq [x (range start-x (inc end-x))]
            (draw-tile x end-y (:tile-map state) (:outline-id view)))
    (doseq [y (range start-y end-y)]
      (draw-tile start-x y (:tile-map state) (:outline-id view))
      (draw-tile end-x y (:tile-map state) (:outline-id view))
      )))

(defn- draw-canvas [view state]
   (doseq [[[x y] id] (:canvas view)]
     (draw-tile x y (:tile-map state) id)))

(defn- make-checkbox [view state])

(defn- draw-checkbox [view state]
   (draw-tile 1 1 (:tile-map state) :823))

(defn draw-canvas-view [view state]
  (draw-view view state)
  (draw-text-relative 1 1 view state "Entity Builder")
  )

(defn- on-click-canvas-view [[mouse-x mouse-y] view state]
  (println (mouse->grid view))
  state)

(defn make-entity-builder-view [position width height outline-id cursor-id state]
  (let [view (make-view
               {:position position
                :width width
                :height height
                :outline-id outline-id
                :cursor-id cursor-id})
        start-x (inc (first (:position view)))
        start-y (inc (second (:position view)))
        end-x (dec (dec (+ start-x (:width view))))
        end-y (dec (dec (+ start-y (:height view))))]
    (-> view
        (assoc :start [start-x start-y])
        (assoc :end [end-x end-y])
        (assoc :draw-fn draw-canvas-view)
        (assoc :on-click-fn on-click-canvas-view))))
