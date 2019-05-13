(ns clj-boc.entities.aspects.attacker)

(defprotocol Attacker
  (attack [this target world]
          "Attack the target."))
