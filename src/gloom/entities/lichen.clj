(ns gloom.entities.lichen
  (:use [gloom.entities.core :only [Entity get-id add-aspect]]
        [gloom.entities.aspects.destructible :only [Destructible]]
        [gloom.entities.aspects.receiver :only [send-message-nearby]]
        [gloom.world :only [find-empty-neighbor]]))

(defrecord Lichen [id glyph color location max-hp hp name])

(defn make-lichen [location]
  (map->Lichen {
                 :id (get-id)
                 :glyph "F"
                 :color :green
                 :location location
                 :max-hp 1
                 :hp 1
                 :name "lichen"}))

(defn should-grow []
  (< (rand) 0.01))

(defn grow [{:keys [location]} world]
  (if-let [target (find-empty-neighbor world location)]
    (let [new-lichen (make-lichen target)
          world (assoc-in world [:entities (:id new-lichen)] new-lichen)
          world (send-message-nearby location "The lichen grows." world)]
      world)
    world))

(extend-type Lichen Entity
  (tick [this world]
        (if (should-grow)
          (grow this world)
          world)))

(add-aspect Lichen Destructible)
