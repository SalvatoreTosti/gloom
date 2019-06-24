(ns gloom.entities.lichen
  (:use [gloom.entities.core :only [Entity get-id add-aspect]]
        [gloom.entities.aspects.destructible :only [Destructible]]
        [gloom.entities.aspects.receiver :only [send-message-nearby]]
        [gloom.world :only [find-empty-neighbor get-entities-around]]
        [gloom.entities.aspects.renderable :only [Renderable]]
        [gloom.entities.aspects.describable :only [Describable]]))

(defrecord Lichen [id glyph color location max-hp hp name type])

(defn make-lichen [location]
  (map->Lichen {
                 :id (get-id)
                 :glyph "F"
                 :color :green
                 :location location
                 :max-hp 1
                 :hp 1
                 :name "lichen"
                 :type :lichen}))

(defn should-grow [this world]
  (let [entities (get-entities-around world (:location this) 5)
        lichens (filter #(= :lichen (:type %)) entities)]
    (println (count lichens))
    (if (< (count lichens) 8)
      (< (rand) 0.01)
      false)))

(defn grow [{:keys [location]} world]
  (if-let [target (find-empty-neighbor world location)]
    (let [new-lichen (make-lichen target)
          world (assoc-in world [:entities (:id new-lichen)] new-lichen)
          world (send-message-nearby location "The lichen grows." world)]
      world)
    world))

(extend-type Lichen Entity
  (tick [this world]
        (if (should-grow this world)
          (grow this world)
          world)))

(add-aspect Lichen Destructible)
(add-aspect Lichen Describable
            (type-name [this]
                       "lichen"))
(add-aspect Lichen Renderable
            (color [this]
                   [99 240 66])
            (image [this]
                   :2))
