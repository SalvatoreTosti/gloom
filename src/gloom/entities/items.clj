(ns gloom.entities.items
  (:use [gloom.entities.core :only [defaspect]]
        [gloom.entities.aspects.container :only [store withdraw]]))

(defn gather [world holder item]
  (-> world
      (update-in [:entities (:id holder) :inventory] #(store % item))
      (assoc-in [:entities (:id holder) :inventory :items (:id item) :location] nil)
      (update-in [:entities] dissoc (:id item))))

(defn dump [world holder item new-location]
  (-> world
      (update-in [:entities (:id holder) :inventory] #(withdraw % item))
      (assoc-in [:entities (:id item)] item)
      (assoc-in [:entities (:id item) :location] new-location)))

(assoc-in {:zed {:ned nil}} [:zed :ned] 1)

(defn delete [world item]
  (-> world
      (update-in [:entities] dissoc (:id item))))

(defn delete [world holder item]
  (-> world
      (dump world item holder nil)
      (delete item)))
