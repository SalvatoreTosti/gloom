(ns clj-boc.entities.aspects.destructible)

(defprotocol Destructible
  (take-damage [this world damage]
               "Take the given amount of damage, update world appropriately"))
