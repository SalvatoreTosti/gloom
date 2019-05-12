(ns clj-boc.entities.aspects.digger)

(defprotocol Digger
  (dig [this world target]
       "Dig a location.")
  (can-dig? [this world target]
            "Return if the entity can dig the new location."))
