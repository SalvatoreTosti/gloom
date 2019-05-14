(ns clj-boc.entities.aspects.destructible
  (:use [clj-boc.entities.core :only [defaspect]]))

(defaspect Destructible
  (take-damage [{:keys [id] :as this} damage world]
               (let [damaged-this (update-in this [:hp] - damage)]
                 (if-not (pos? (:hp damaged-this))
                   (update-in world [:entities] dissoc id)
                   (assoc-in world [:entities id] damaged-this)))))
