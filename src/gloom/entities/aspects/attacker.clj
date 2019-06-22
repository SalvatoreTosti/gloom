(ns gloom.entities.aspects.attacker
  (:use [gloom.entities.core :only [defaspect]]
        [gloom.entities.aspects.destructible :only [Destructible take-damage defense-value]]
        [gloom.entities.aspects.receiver :only [send-message]]
        [gloom.spells.effects.damage :only [make-damage]]
        [gloom.spells.effects.dice-damage :only [make-dice-damage]]
        [gloom.spells.spell :only [trigger-spell make-spell]]))

(defaspect Attacker
  (attack [this target world]
          (-> (make-spell [(make-dice-damage)])
              (trigger-spell this target world))))

