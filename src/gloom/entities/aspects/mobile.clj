(ns gloom.entities.aspects.mobile
  (:use [gloom.world :only [is-empty?]]
        [gloom.entities.core :only [defaspect]]))

(defaspect Mobile
  (move [this dest world]
        {:pre [(can-move? this dest world)]}
        (assoc-in world [:entities (:id this) :location] dest))
  (can-move? [this dest world]
             (is-empty? world dest)))


