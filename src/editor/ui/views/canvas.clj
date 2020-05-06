(ns editor.ui.views.canvas
  (:use
    [gloom.ui.core :only [draw-tile]]
    [editor.ui.views.core :only [make-view mouse->grid draw-text-relative]]
    [gloom.entities.aspects.renderable :only  [color image]]))


(defn- draw [view state]
  (doseq [[[x y] entity] (:canvas view)]
    (when entity
     (draw-tile x y (:tile-map state) (image entity) (color entity)))))

(defn- on-click [[mouse-x mouse-y] view state]
  (let [generator-map (get-in state [:editor :views (:palette-view-id view) :entity-generators])
        selected-id (get-in state [:editor :views (:palette-view-id view) :selected-id]) 
        generator (selected-id generator-map) 
        new-entity (generator [0,0])
        ]
    (assoc-in
    state
    [:editor :views (:id view) :canvas (mouse->grid view)]
    new-entity)))

(defn- make-blank-canvas [[start-x start-y] [end-x end-y]]
  (into {}
        (for
          [x (range start-x end-x)
           y (range start-y end-y)]
          {[x y] nil})))

(defn make-canvas-view
  [{:keys [palette-view] :as view-data} state]
  (let [view (make-view view-data)]
      (assoc
        view
        :kind :canvas
        :draw-fn draw
        :on-click-fn on-click
        :palette-view-id (:id palette-view)
        :canvas (make-blank-canvas (:start view) (:end view)))))

(defn unpickle-canvas-view [pickled-view]
  (-> pickled-view
      (make-canvas-view nil)
      (assoc :palette-view-id (:palette-view-id pickled-view))
      (assoc :canvas (:canvas pickled-view))))

(defn pickle-canvas-view [view]
  {
    :id (:id view)
    :kind (:kind view)
    :position (:position view)
    :width (:width view)
    :height (:height view)
    :outline-id (:outline-id view)
    :cursor-id (:cursor-id view)
    :pixel-coordinates (:pixel-coordinates view)
    :canvas (:canvas view)
    :palette-view-id (:palette-view-id view)
   })
