(ns editor.ui.views.canvas
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
     (draw-tile x y (:tile-map state) id))
;;   (draw-text-relative 4 4 view state "canvas")
  )

(defn draw-canvas-view [view state]
  (draw-view view state)
  (draw-canvas view state))

(defn- on-click-canvas-view [[mouse-x mouse-y] view state]
;;   (println (:end view))
  (assoc-in
    state
    [:editor :views (:id view) :canvas (mouse->grid view)]
    (:pen-tool-id view)))

(defn- make-blank-canvas [[start-x start-y] [end-x end-y]]
  (println [start-x start-y] [end-x end-y])
  (let [positions (for [x (range start-x end-x)
                        y (range start-y end-y)]
                    {[x y] :0})]
    (println positions)
    (into {} positions)))
;;     (draw-tile x y (:tile-map state) id)))

(defn make-canvas-view [position width height outline-id cursor-id state]
  (let [view (make-view position width height outline-id cursor-id)
        start-x (inc (first (:position view)))
        start-y (inc (second (:position view)))
        end-x (dec (dec (+ start-x (:width view))))
        end-y (dec (dec (+ start-y (:height view))))]
    (-> view
        (assoc :start [start-x start-y])
        (assoc :end [end-x end-y])
        (assoc :pen-tool-id :2)
        (assoc :canvas (make-blank-canvas [start-x start-y] [end-x end-y]))
        (assoc :draw-fn draw-canvas-view)
        (assoc :on-click-fn on-click-canvas-view))))
