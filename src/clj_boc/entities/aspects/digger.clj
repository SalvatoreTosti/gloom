(ns clj-boc.entities.aspects.digger)

(defprotocol Digger
  (dig [this target world]
       "Dig a location.")
  (can-dig? [this target world]
            "Return if the entity can dig the new location."))
