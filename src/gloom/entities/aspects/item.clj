(ns gloom.entities.aspects.item
  (:use [gloom.entities.core :only [defaspect]]))

(defaspect Item
  (drop-it [this holder world]
           (let [world (update-in world [:entities (:id holder) :inventory] dissoc (:id this))
                 world (assoc-in world [:entities (:id this)] this)
                 world (assoc-in world [:entites (:id this) :location] (:location holder))]
           world))
  (pick-up [{:keys [id] :as this} holder world]
           (let [world (update-in world [:entities] dissoc id)
                 world (assoc-in world [:entities (:id holder) :inventory id] this)]
             (println (get-in world [:entities (:id holder) :inventory]))
             world
             )))
