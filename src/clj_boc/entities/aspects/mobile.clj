(ns clj-boc.entities.aspects.mobile
  (:use [clj-boc.world :only [is-empty?]]
        [clj-boc.entities.core :only [defaspect]]))

(defaspect Mobile
  (move [this dest world]
        {:pre [(can-move? this dest world)]}
        (assoc-in world [:entities (:id this) :location] dest))
  (can-move? [this dest world]
             (is-empty? world dest)))


