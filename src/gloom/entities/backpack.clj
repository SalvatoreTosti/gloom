(ns gloom.entities.backpack
  (:use [gloom.entities.core :only [Entity get-id add-aspect]]
        [gloom.entities.aspects.container :only [Container]]))

(defrecord Backpack [id glyph color location])

(defn make-backpack
  ([location]
  (map->Backpack {
                   :id (get-id)
                   :glyph "b"
                   :color :green
                   :location location
                   :items {}
                   :size 10}))
  ([]
   (make-backpack nil)))

(add-aspect Backpack Container)
