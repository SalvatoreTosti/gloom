(ns gloom.entities.player
  (:use [gloom.entities.core :only [Entity add-aspect]]
        [gloom.entities.aspects.mobile :only [Mobile move can-move?]]
        [gloom.entities.aspects.digger :only [Digger dig can-dig?]]
        [gloom.entities.aspects.destructible :only [Destructible]]
        [gloom.entities.aspects.attacker :only [Attacker attack]]
        [gloom.entities.aspects.receiver :only [Receiver]]
        [gloom.entities.aspects.leveler :only [Leveler add-exp]]
        [gloom.entities.aspects.item :only [Item pick-up]]


        [gloom.world :only [find-empty-tile get-tile-kind set-tile-floor is-empty? get-entity-at]]
        [gloom.coordinates :only [destination-coords]]))

(defrecord Player [id glyph color location max-hp hp attack exp])

(defn make-player [world]
  (map->Player {
                 :id :player
                 :glyph "@"
                 :color :yellow
                 :location (find-empty-tile world)
                 :max-hp 10
                 :hp 10
                 :attack 2
                 :exp 0}))

(extend-type Player Entity
  (tick [this world]
        world))

(defn view-inventory [this]
  (get-in this [:inventory :name]))


(add-aspect Player Mobile)

(defn move-player [world dir]
  (let [player (get-in world [:entities :player])
        target (destination-coords (:location player) dir)
        entity-at-target (get-entity-at world target)]
    (cond
      (satisfies? Item entity-at-target) (pick-up entity-at-target player world)
      entity-at-target (attack player entity-at-target world)
      (can-move? player target world) (move player target world)
      (can-dig? player target world) (dig player target world)
      :else world)))

(add-aspect Player Digger)
(add-aspect Player Attacker)
(add-aspect Player Receiver)
(add-aspect Player Leveler)