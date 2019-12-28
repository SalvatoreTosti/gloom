(ns editor.ui.views.canvas
  (:use
    [gloom.ui.core :only [draw-tile]]
    [editor.ui.views.core :only [make-view mouse->grid draw-text-relative]]))

(defn- draw [view state]
  (doseq [[[x y] id] (:canvas view)]
     (draw-tile x y (:tile-map state) id)))

(defn- on-click [[mouse-x mouse-y] view state]
  (assoc-in
    state
    [:editor :views (:id view) :canvas (mouse->grid view)]
    (get-in state [:editor :views (:palette-view-id view) :selected-id])))

(defn- make-blank-canvas [[start-x start-y] [end-x end-y]]
  (into {}
        (for
          [x (range start-x end-x)
           y (range start-y end-y)]
          {[x y] :0})))

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

(defn unpickle-canvas-view [pickled-view]
  (let [view (make-view pickled-view)]
      (assoc
        view
        :kind :canvas
        :draw-fn draw
        :on-click-fn on-click
        :palette-view-id (:palette-view-id pickled-view)
        :canvas (:canvas pickled-view))))


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
