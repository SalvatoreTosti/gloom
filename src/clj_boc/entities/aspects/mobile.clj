(ns clj-boc.entities.aspects.mobile)

(defprotocol Mobile
  (move [this dest world]
        "Move this entity to a new location.")
  (can-move? [this dest world]
             "Return if the entity can move to a new location."))
