(ns clj-boc.entities.lichen
  (:use [clj-boc.entities.core :only [Entity get-id]]))

(defrecord Lichen [id glyph color location])

(defn make-lichen [location]
  (->Lichen (get-id) "F" :green location))

(defn should-grow []
  true)

(extend-type Lichen Entity
  (tick [this world]
        (if (should-grow)
          world)))
