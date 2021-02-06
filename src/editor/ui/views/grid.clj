(ns editor.ui.views.grid
  (:use
   [gloom.ui.core :only [draw-tile]]
   [editor.ui.views.core :only [make-view mouse->grid draw-view-outline]]
   [gloom.entities.apple :only [make-apple]]
   [gloom.entities.aspects.renderable :only  [color image]]))

(defn- make-generator-map []
  {:Apple  make-apple,
   :Apple2 make-apple})

(def generator-map
  (memoize make-generator-map))

(defn build-image-positions
  ([view items]
   (build-image-positions (:start view) (:end view) items))

  ([[start-x start-y] [end-x end-y] items]
   (let [positions (for [y (range start-y end-y)
                         x (range start-x end-x)]
                     [x y])]
     (->> items
          (zipmap positions)
          (into {})))))

(defn build-entity-positions [view]
  (->> (generator-map)
       keys
       (build-image-positions view)))

(defn- draw-entity-grid [view state]
  (doseq [[[x y] entity-type] (:entity-positions view)]
    (let [entity-generator (entity-type (generator-map))
          entity (entity-generator [0,0])]
      (draw-tile x y (:tile-map state) (image entity) (color entity)))))

(defn- draw [view state]
  (draw-view-outline view state)
  (draw-entity-grid view state))

(defn- on-click [[mouse-x mouse-y] view state]
  (if-let [entity-type (get (:entity-positions view) (mouse->grid view))]
    (let [entity-generator (entity-type (generator-map))
          entity (entity-generator [0,0])]
      (if entity
        (assoc-in state [:editor :views (:id view) :selected-id] entity-type)
        state))
    state))

(defn make-grid-view [{:keys [] :as view-data} state]
  (let [view (make-view view-data)
        display-ids (->> state
                         :tile-map
                         keys
                         (sort-by #(bigdec (name %))))]
    (assoc
     view
     :entity-positions (build-entity-positions view)
     :entity-generators (generator-map)
     :kind :grid
     :selected-id :2
     :display-ids display-ids
     :draw-fn draw
     :on-click-fn on-click)))

(defn pickle-grid-view [view]
  (select-keys
   view
   [:id
    :kind
    :position
    :width
    :height
    :outline-id
    :pixel-coordinates
    :selected-id
    :display-ids
    :entity-positions
    :entity-generators]))

(defn unpickle-grid-view [pickled-view]
  (assoc
   (make-view pickled-view)
   :selected-id (:selected-id pickled-view)
   :kind :grid
   :display-ids (:display-ids pickled-view)
   :entity-positions (:entity-positions pickled-view)
   :entity-generators (:entity-generators pickled-view)
   :draw-fn draw
   :on-click-fn on-click))

