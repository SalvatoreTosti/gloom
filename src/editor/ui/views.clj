(ns editor.ui.views
   (:use
     [editor.ui.core :only [get-id]]
     [gloom.ui.core :only [clear-screen draw-tile tile-size]]
     ))

(defn- make-view [[x y] width height outline-id cursor-id]
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

(defn- build-image-positions [[start-x start-y] [end-x end-y] display-ids]
  (let [positions (for [y (range start-y end-y)
                        x (range start-x end-x)]
                    [x y])
        position-ids (zipmap positions display-ids)
        position-ids (into [] position-ids)]
  position-ids))

(defn- draw-image-grid [[start-x start-y] [end-x end-y] state display-ids]
  (doseq [[[x y] id] (build-image-positions
                       [start-x start-y]
                       [end-x end-y]
                       display-ids)]
    (draw-tile x y (:tile-map state) id)))

(defn draw-list-view [view state]
  (draw-view view state)
  (let [
         start-x (inc (first (:position view)))
         start-y (inc (second (:position view)))
         end-x (dec (dec (+ start-x (:width view))))
         end-y (dec (dec (+ start-y (:height view))))
      ]
    (draw-image-grid
      [start-x start-y]
      [end-x end-y]
      state
      (->> state
           :tile-map
           keys
           (sort-by #(bigdec (name %)))))))

(defn make-grid-view [position width height outline-id cursor-id]
  (let [view (make-view position width height outline-id cursor-id)]
    (-> view
        (assoc :draw-fn draw-list-view)
    )))
