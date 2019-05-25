(ns gloom.entities.aspects.item
  (:use [gloom.entities.core :only [defaspect]]))

(defaspect Item
  (drop-it [{:keys [id] :as this} holder world]
           (-> world
               (update-in [:entities (:id holder) :inventory] dissoc id)
               (assoc-in [:entities id] this)
               (assoc-in [:entites id :location] (:location holder))))
  (pick-up [{:keys [id] :as this} holder world]
           (-> world
               (update-in [:entities] dissoc id)
               (assoc-in [:entities (:id holder) :inventory id] this))))
