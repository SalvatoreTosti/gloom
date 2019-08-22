(ns editor.ui.views.grid
  (:use
    [gloom.ui.core :only [draw-tile]]
    [editor.ui.views.core :only [make-view mouse->grid]]))

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
        position-ids (into {} position-ids)]
  position-ids))

(defn- build-image-grid [[start-x start-y] [end-x end-y] state display-ids]
  (build-image-positions [start-x start-y] [end-x end-y] display-ids))

(defn- draw-image-grid [[start-x start-y] [end-x end-y] state display-ids]
  (doseq [[[x y] id] (build-image-positions
                       [start-x start-y]
                       [end-x end-y]
                       display-ids)]
    (draw-tile x y (:tile-map state) id)))

(defn draw-grid-view [view state]
  (draw-view view state)
  (draw-image-grid
    (:start view)
    (:end view)
    state
    (:display-ids view)))

(defn- on-click-grid-view [[mouse-x mouse-y] view state]
  (let [tile-id (get (:item-positions view) (mouse->grid view))]
    (if tile-id
      (assoc-in state [:editor :views (:id view) :selected-id] tile-id)
      state)))

(defn make-grid-view [position width height outline-id cursor-id state]
  (let [view (make-view position width height outline-id cursor-id)
        start-x (inc (first (:position view)))
        start-y (inc (second (:position view)))
        end-x (dec (dec (+ start-x (:width view))))
        end-y (dec (dec (+ start-y (:height view))))
        display-ids (->> state
                         :tile-map
                         keys
                         (sort-by #(bigdec (name %))))
        item-positions (build-image-positions [start-x start-y] [end-x end-y] display-ids)]
    (-> view
        (assoc :start [start-x start-y])
        (assoc :end [end-x end-y])
        (assoc :selected-id :2)
        (assoc :display-ids display-ids)
        (assoc :item-positions item-positions)
        (assoc :draw-fn draw-grid-view)
        (assoc :on-click-fn on-click-grid-view))))
