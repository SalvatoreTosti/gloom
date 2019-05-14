(ns clj-boc.entities.aspects.destructible
  (:use [clj-boc.entities.core :only [defaspect]]))

(defaspect Destructible
  (take-damage [{:keys [id] :as this} damage world]
               (let [damaged-this (update-in this [:hp] - damage)]
                 (if-not (pos? (:hp damaged-this))
                   (update-in world [:entities] dissoc id)
                   (update-in world [:entities id] assoc damaged-this)))))

(defprotocol Destructible
  (take-damage [this damage world]
               "Take the given amount of damage, update world appropriately")

  (defense-value [this world]
    (get this :defense 0)))
