(ns editor.ui.views.grid
  (:use
    [gloom.ui.core :only [draw-tile]]
    [editor.ui.views.core :only [make-view mouse->grid draw-view-outline]]
    [gloom.entities.apple :only [make-apple]] 
    [gloom.entities.aspects.renderable :only  [color image]]
    ))

(defn make-generator-map []
  {:Apple  make-apple,
   :Apple2 make-apple} 
  )

(def generator-map
  (memoize make-generator-map))

(defn- build-image-positions [[start-x start-y] [end-x end-y] items]
  (let [positions (for [y (range start-y end-y)
                        x (range start-x end-x)]
                    [x y])]
    (->> items 
         (zipmap positions)
         (into {}))))

(defn build-entity-positions [view]
  (let [generator-map (generator-map)
        entity-keys (keys generator-map) 
        entity-positions-map (build-image-positions (:start view) (:end view) entity-keys)
        ]
    entity-positions-map 
    )
  )

(defn draw-entity-grid [view state]
    (doseq [[[x y] entity-type] (:entity-positions view)]  
      (let [entity-generator (entity-type (generator-map))
            entity (entity-generator [0,0]) 
            ] 
        (draw-tile x y (:tile-map state) (image entity) (color entity))   
        )  
      )
    )

(defn- draw [view state]
  (draw-view-outline view state)
  (draw-entity-grid view state)
  )

(defn- on-click [[mouse-x mouse-y] view state]
  (let [tile-id (get (:item-positions view) (mouse->grid view))]
    (if tile-id
      (assoc-in state [:editor :views (:id view) :selected-id] tile-id)
      state)))

(defn make-grid-view [{:keys [] :as view-data} state]
  (let [view (make-view view-data)
        display-ids (->> state
                         :tile-map
                         keys
                         (sort-by #(bigdec (name %))))]
        (assoc
          view
          :entity-positions (build-entity-positions view)  
          
          :kind :grid
          :selected-id :2
          :display-ids display-ids
          :item-positions (build-image-positions (:start view) (:end view) display-ids)
          :draw-fn draw
          :on-click-fn on-click)))

(defn pickle-grid-view [view]
  {
    :id (:id view)
    :kind (:kind view)
    :position (:position view)
    :width (:width view)
    :height (:height view)
    :outline-id (:outline-id view)
    :cursor-id (:cursor-id view)
    :pixel-coordinates (:pixel-coordinates view)
    :selected-id (:selected-id view)
    :display-ids (:display-ids view)
    :item-positions (:item-positions view)
    })

(defn unpickle-grid-view [pickled-view]
  (let [view (make-view pickled-view)]
        (assoc
          view
;;           :selected-id :2
          :kind :grid
          :display-ids (:display-ids pickled-view)
          :item-positions (:item-positions pickled-view)
          :draw-fn draw
          :on-click-fn on-click)))
