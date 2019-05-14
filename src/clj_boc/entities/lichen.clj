(ns clj-boc.entities.lichen
  (:use [clj-boc.entities.core :only [Entity get-id add-aspect]]
        [clj-boc.entities.aspects.destructible :only [Destructible]]
        [clj-boc.world :only [find-empty-neighbor]]))

(defrecord Lichen [id glyph color location hp])

(defn make-lichen [location]
  (map->Lichen {
                 :id (get-id)
                 :glyph "F"
                 :color :green
                 :location location
                 :max-hp 1
                 :hp 1}))

(defn should-grow []
  (< (rand) 0.01))

(defn grow [lichen world]
  (if-let [target (find-empty-neighbor world (:location lichen))]
    (let [new-lichen (make-lichen target)]
      (assoc-in world [:entities (:id new-lichen)] new-lichen))
    world))

(extend-type Lichen Entity
  (tick [this world]
        (if (should-grow)
          (grow this world)
          world)))

(add-aspect Lichen Destructible)
