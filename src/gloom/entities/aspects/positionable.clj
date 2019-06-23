(ns gloom.entities.aspects.positionable
    (:use [gloom.entities.core :only [defaspect]]
          [gloom.world :only [find-empty-tile]]))

(defaspect Positionable
  (position [this world]
            (->> world
                 (find-spawn-location this)
                 (assoc this :location)))

  (find-spawn-location [this world]
                       (find-empty-tile world)))
