(ns clj-boc.entities.lichen
  (:use [clj-boc.entities.core :only [Entity get-id]]
        [clj-boc.entities.aspects.destructible :only [Destructible take-damage]]))

(defrecord Lichen [id glyph color location hp])

(defn make-lichen [location]
  (->Lichen (get-id) "F" :green location 1))

(defn should-grow []
  true)

(extend-type Lichen Entity
  (tick [this world]
        (if (should-grow)
          world)))

(extend-type Lichen Destructible
  (take-damage [{:keys [id] :as this} world damage]
               (let [damaged-this (update-in this [:hp] - damage)]
                 (if-not (pos? (:hp damaged-this))
                   (update-in world [:entities] dissoc id)
                   (update-in world [:entities id] assoc damaged-this)))))
