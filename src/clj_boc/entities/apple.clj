(ns clj-boc.entities.apple
  (:use [clj-boc.entities.core :only [Entity get-id add-aspect]]
        [clj-boc.entities.aspects.item :only [Item drop-it]]))

(defrecord Apple [id glyph color location name])

(defn make-apple [location]
  (map->Apple{
               :id (get-id)
               :glyph "a"
               :color :red
               :location location
               :name "apple"}))

(extend-type Apple Entity
  (tick [this world]
        world))

(add-aspect Apple Item)

