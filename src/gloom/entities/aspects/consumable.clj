(ns gloom.entities.aspects.consumable
  (:use [gloom.entities.core :only [defaspect]]))

(defaspect Consumable
  (consume-inventory [{:keys [id] :as this} consumer world]
                 (let [world (consume-effect this consumer world)
                       world (update-in world [:entities (:id this) :inventory] dissoc id)]
                   world))


  (consume-world [{:keys [id] :as this} consumer world]
                     (let [world (consume-effect this consumer world)
                           world (update-in world [:entities] dissoc id)]
                       world))

  (consume-effect [this consumer world]
                  world)

  (can-consume? [this consumer world]
             true))
