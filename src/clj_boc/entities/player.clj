(ns clj-boc.entities.player
  (:use [clj-boc.entities.core :only [Entity add-aspect]]
        [clj-boc.entities.aspects.mobile :only [Mobile move can-move?]]
        [clj-boc.entities.aspects.digger :only [Digger dig can-dig?]]
        [clj-boc.coordinates :only [destination-coords]]
        [clj-boc.world :only [find-empty-tile get-tile-kind set-tile-floor is-empty? get-entity-at]]
        [clj-boc.entities.aspects.destructible :only [Destructible]]
        [clj-boc.entities.aspects.attacker :only [Attacker attack]]))

(defrecord Player [id glyph color location])

(defn make-player [world]
  (map->Player {
                 :id :player
                 :glyph "@"
                 :color :yellow
                 :location (find-empty-tile world)
                 :max-hp 10
                 :hp 10}))


(defn check-tile
  [world dest pred]
  (pred (get-tile-kind world dest)))

(extend-type Player Entity
  (tick [this world]
        world))

(add-aspect Player Mobile)

(defn move-player [world dir]
  (let [player (get-in world [:entities :player])
        target (destination-coords (:location player) dir)
        entity-at-target (get-entity-at world target)]
    (cond
      entity-at-target (attack player entity-at-target world)
      (can-move? player target world) (move player target world)
      (can-dig? player target world) (dig player target world)
      :else world)))

(extend-type Player Digger
  (dig [this dest world]
       {:pre [(can-dig? this dest world)]}
       (set-tile-floor world dest))
  (can-dig? [this dest world]
            (check-tile world dest #{:wall})))

(add-aspect Player Attacker)
