(ns gloom.entities.bunny
  (:use [gloom.entities.core :only [Entity get-id add-aspect]]
        [gloom.entities.aspects.mobile :only [Mobile move can-move?]]
        [gloom.entities.aspects.destructible :only [Destructible]]
        [gloom.world :only [find-empty-tile find-empty-neighbor]]
        [gloom.entities.aspects.renderable :only [Renderable]]
        [gloom.entities.aspects.describable :only [Describable]]))

(defrecord Bunny [id glyph color location max-hp hp name])

(defn make-bunny [location]
  (map->Bunny {:id (get-id)
               :glyph "v"
               :color :yellow
               :location location
               :max-hp 4
               :hp 4
               :name "bunny"}))

(defn should-move []
  (< (rand) 0.5))

(defn get-move-location [bunny world]
  (if-let [target (find-empty-neighbor world (:location bunny))]
    target
    (:location bunny)))

(extend-type Bunny Entity
             (tick [this world]
               (let [move-location (get-move-location this world)]
                 (cond
                   (not (should-move)) world
                   (not (can-move? this move-location world)) world
                   :else (move this (get-move-location this world) world)))))

(add-aspect Bunny Mobile)
(add-aspect Bunny Destructible)
(add-aspect Bunny Describable
            (type-name [this]
                       "bunny"))
(add-aspect Bunny Renderable
            (color [this]
                   :pink)
            (image [this]
                   :255))

