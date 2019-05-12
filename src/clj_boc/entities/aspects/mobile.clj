(ns clj-boc.entities.aspects.mobile)

(defprotocol Mobile
  (move [this world dest]
        "Move this entity to a new location.")
  (can-move? [this world dest]
             "Return if the entity can move to a new location."))
