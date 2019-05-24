(ns gloom.entities.aspects.equipable
    (:use [gloom.entities.core :only [defaspect]]))
;;           [gloom.world :only [set-tile-floor check-tile]]))

(defaspect Equipable
  (equip [this target world]
         {:pre [(satisfies? Equipper target)]}
         (let [ equip-type (:type this)
                current-equip (equip-type target)
               (if (nil? current-equip)
                 (assoc-in target equip-type this)
                 (
         ((:type this)
       (set-tile-floor world dest))
  (can-equip
