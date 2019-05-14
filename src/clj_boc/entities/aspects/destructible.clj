(ns clj-boc.entities.aspects.destructible)

(defprotocol Destructible
  (take-damage [this damage world]
               "Take the given amount of damage, update world appropriately")

  (defense-value [this world]
    (get this :defense 0)))
