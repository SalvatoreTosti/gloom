(ns gloom.entities.aspects.describable
  (:use [gloom.entities.core :only [defaspect]]))

(defn give-name [entity new-name]
  (assoc entity :special-name new-name))

(defaspect Describable
  (type-name [this]
             "thing"))

(defn get-name [this]
  (if (nil? (:special-name this))
    (type-name this)
    (:special-name this)))




