(ns gloom.entities.aspects.digger
  (:use [gloom.entities.core :only [defaspect]]
        [gloom.world :only [set-tile-floor check-tile]]))

(defaspect Digger
  (dig [this dest world]
       {:pre [(can-dig? this dest world)]}
       (set-tile-floor world dest))
  (can-dig? [this dest world]
            (check-tile world dest #{:wall})))
