(ns gloom.spells.damage
  (:use [gloom.spells.effect :only [Effect]]
        [gloom.entities.core :only [get-id add-aspect]]
        [gloom.entities.aspects.destructible :only [Destructible take-damage defense-value]]
        [gloom.entities.aspects.receiver :only [send-message]]))

(defrecord Damage [id name])

(defn make-damage []
  (map->Damage{:id (get-id)
               :name "damage"}))

(defn attack-value [caster world]
  (get caster :attack 1))

(defn get-damage [attacker target world]
  (let [attack (attack-value attacker world)
        defense (defense-value target world)
        max-damage (max 0 (- attack defense))
        damage (inc (rand-int max-damage))]
    damage))

(add-aspect Damage Effect
            (apply-effect [this caster target world]
                          {:pre [(satisfies? Destructible target)]}
                          (let [damage (get-damage caster target world)]
                            (->> world
                                 (take-damage target damage)
                                 (send-message caster "%s damages the %s for %d damage!" [(:id caster) (:id target) damage])
                                 (send-message target "%s damages %s for %d damage!" [(:id target) (:id caster) damage])))))