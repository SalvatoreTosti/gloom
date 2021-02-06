(ns gloom.entities.aspects.container
  (:use [gloom.entities.core :only [defaspect]]))

(defaspect Container
  (get-size [this]
            (:size this))

  (full? [this]
         (>= (count (:items this)) (:size this)))

  (store [this item]
         (if (full? this)
           this
           (assoc-in this [:items (:id item)] item)))

  (fetch [this id]
         (get-in this [:items id]))

  (withdraw [this item]
            (update-in this [:items] dissoc (:id item))))
