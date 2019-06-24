(ns gloom.entities.aspects.destructible
  (:use [gloom.entities.core :only [defaspect]]))

(defaspect Destructible
  (destroy [{:keys [id] :as this} world]
           (update-in world [:entities] dissoc id))
  (take-damage [{:keys [id] :as this} damage world]
               (let [damaged-this (update-in this [:hp] - damage)]
                 (if-not (pos? (:hp damaged-this))
                   (destroy this world)
                   (assoc-in world [:entities id] damaged-this))))
  (defense-value [this world]
    (get this :defense 0)))
