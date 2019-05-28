(ns gloom.entities.aspects.attacker
  (:use [gloom.entities.core :only [defaspect]]
        [gloom.entities.aspects.destructible :only [Destructible take-damage defense-value]]
        [gloom.entities.aspects.receiver :only [send-message]]
        [gloom.spells.damage :only [make-damage]]
        [gloom.spells.spell :only [trigger-spell make-spell]]))

(declare get-damage)

(defaspect Attacker
  (attack [this target world]
          (-> (make-spell [(make-damage)])
              (trigger-spell this target world))))
