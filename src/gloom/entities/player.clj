(ns gloom.entities.player
  (:use [gloom.entities.core :only [Entity add-aspect]]
        [gloom.entities.aspects.mobile :only [Mobile move can-move?]]
        [gloom.entities.aspects.digger :only [Digger dig can-dig?]]
        [gloom.entities.aspects.destructible :only [Destructible]]
        [gloom.entities.aspects.attacker :only [Attacker attack]]
        [gloom.entities.aspects.receiver :only [Receiver send-message]]
        [gloom.entities.aspects.leveler :only [Leveler add-exp]]
        [gloom.entities.aspects.item :only [Item]]
        [gloom.entities.aspects.consumable :only [Consumable consume-world]]
        [gloom.world :only [find-empty-neighbor find-empty-tile get-tile-kind set-tile-floor is-empty? get-entity-at]]
        [gloom.entities.items :only [gather dump]]
        [gloom.entities.aspects.container :only [fetch withdraw full?]]
        [gloom.coordinates :only [destination-coords]]
        [gloom.entities.aspects.renderable :only [Renderable]]))

(defrecord Player [id glyph color location max-hp hp attack exp image])

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

(defn pick-up-item [world player item]
  (if (full? (:inventory player))
    (send-message player "You can't hold any more items!" nil world)
    (gather world player item)))

(defn move-player [world dir]
  (let [player (get-in world [:entities :player])
        target (destination-coords (:location player) dir)
        entity-at-target (get-entity-at world target)]
    (cond
      (satisfies? Item entity-at-target) (pick-up-item world player entity-at-target)
      (satisfies? Consumable entity-at-target) (consume-world entity-at-target player world)

      entity-at-target (attack player entity-at-target world)
      (can-move? player target world) (move player target world)
      (can-dig? player target world) (dig player target world)
      :else world)))

(defn drop-item [world id]
   (let [player (get-in world [:entities :player])
         target (find-empty-neighbor world (:location player))
         item (fetch (:inventory player) id)]
     (if target
       (dump world player item target)
       (send-message player "There's no room to drop the item!" nil world))))

(add-aspect Player Digger)
(add-aspect Player Attacker)
(add-aspect Player Receiver)
(add-aspect Player Leveler)
(add-aspect Player Renderable
            (color [this]
                   [99 240 66])
            (image [this]
                   :31))
