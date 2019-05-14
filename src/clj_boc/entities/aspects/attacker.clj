(ns clj-boc.entities.aspects.attacker)

(defprotocol Attacker
  (attack [this target world]
          "Attack the target.")

  (attack-value [this world]
    (get this :attack 1)))
