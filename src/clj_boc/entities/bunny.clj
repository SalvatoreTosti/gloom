(ns clj-boc.entities.bunny
  (:use [clj-boc.entities.core :only [Entity get-id add-aspect]]
        [clj-boc.entities.aspects.mobile :only [Mobile move can-move?]]
        [clj-boc.entities.aspects.destructible :only [Destructible]]
        [clj-boc.world :only [find-empty-tile find-empty-neighbor]]))


(defrecord Bunny [id glyph color location max-hp hp name])

(defn make-bunny [location]
  (map->Bunny {
                 :id (get-id)
                 :glyph "v"
                 :color :yellow
                 :location location
                 :max-hp 4
                 :hp 4
                 :name "bunny"}))

(defn should-move []
  (< (rand) 0.01))

(defn get-move-location [bunny world]
  (if-let [target (find-empty-neighbor world (:location bunny))]
    target
    (:location bunny)))

(extend-type Bunny Entity
  (tick [this world]
        (if (should-move)
          (move this (get-move-location this world) world)
          world)))

(add-aspect Bunny Mobile)
(add-aspect Bunny Destructible)

