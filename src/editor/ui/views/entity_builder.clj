(ns editor.ui.views.entity-builder
  (:use
    [gloom.ui.core :only [draw-tile]]
    [editor.ui.views.core :only [make-view mouse->grid draw-text-relative draw-view-outline]]))

(defn- draw-canvas [view state]
   (doseq [[[x y] id] (:canvas view)]
     (draw-tile x y (:tile-map state) id)))

(defn- make-checkbox [view state])

(defn- draw-checkbox [view state]
   (draw-tile 1 1 (:tile-map state) :823))

(defn draw-canvas-view [view state]
  (draw-view-outline view state)
  (draw-text-relative 1 1 view state "Entity Builder")
  )

(defn- on-click-canvas-view [[mouse-x mouse-y] view state]
  state)

(defn make-entity-builder-view [{:keys [] :as view-data} state]
  (assoc
    (make-view view-data)
    :kind :entity-builder
    :draw-fn draw-canvas-view
    :on-click-fn on-click-canvas-view))

(defn pickle-entity-builder-view [view]
  {
    :id (:id view)
    :kind (:kind view)
    :position (:position view)
    :width (:width view)
    :height (:height view)
    :outline-id (:outline-id view)
    :cursor-id (:cursor-id view)
    :pixel-coordinates (:pixel-coordinates view)
    })

(defn unpickle-entity-builder-view [pickled-view]
 (assoc
    (make-view pickled-view)
    :draw-fn draw-canvas-view
    :on-click-fn on-click-canvas-view))
