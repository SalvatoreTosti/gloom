(ns gloom.spells.effects.dice-damage
  (:use [gloom.spells.effects.effect :only [Effect]]
        [gloom.entities.core :only [get-id add-aspect]]
        [gloom.entities.aspects.destructible :only [Destructible take-damage defense-value]]
        [gloom.entities.aspects.receiver :only [send-message]]
        [gloom.entities.aspects.describable :only [get-name]]))

(defrecord Damage [id name])

(defn make-dice-damage []
  (map->Damage{:id (get-id)
               :name "dice damage"}))

(defn roll-dice [[dice-type dice-count]]
  (let [dice-range (case dice-type
                     :d4 (range 1 5)
                     :d6 (range 1 7)
                     (range 1 7))
        rolls (take dice-count (repeatedly #(rand-nth dice-range)))
        amount (reduce + rolls)]
    {:dice dice-type :rolls rolls :amount amount}))

(defn attack-values [caster world]
  (->> (get caster :attack-dice {:d4 2})
       seq
       (map roll-dice)))

(defn get-damage [attacker target world]
  (let [attack-rolls (attack-values attacker world)
        attack (->> attack-rolls
                    (map :amount)
                    (reduce +))
        defense (defense-value target world)
        damage (max 0 (- attack defense))]
    damage))

(add-aspect Damage Effect
            (apply-effect [this caster target world]
                          {:pre [(satisfies? Destructible target)]}
                          (let [damage (get-damage caster target world)]
                            (->> world
                                 (take-damage target damage)
                                 (send-message caster "%s damages the %s for %d damage!" [(get-name caster) (get-name target) damage])
                                 (send-message target "%s damages %s for %d damage!" [(get-name target) (get-name caster) damage])))))
