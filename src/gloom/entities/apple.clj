(ns gloom.entities.apple
  (:use [gloom.entities.core :only [Entity get-id add-aspect]]
        [gloom.entities.aspects.item :only [Item drop-it]]
        [gloom.entities.aspects.consumable :only [Consumable]]
        [gloom.entities.aspects.renderable :only [Renderable]]))

(defrecord Apple [id glyph color location name])

(defn make-apple [location]
  (map->Apple {:id (get-id)
               :glyph "a"
               :color :red
               :location location
               :name "apple"}))

(extend-type Apple Entity
             (tick [this world]
               world))

(add-aspect Apple Item)
(add-aspect Apple Consumable
            (consume-effect [this consumer world]
                            (update-in world [:entities (:id consumer) :hp] inc)))
(add-aspect Apple Renderable
            (color [this]
                   :red)
            (image [this]
                   :943))
