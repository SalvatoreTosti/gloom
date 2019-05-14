(ns clj-boc.entities.aspects.attacker
  (:use [clj-boc.entities.core :only [defaspect]]
        [clj-boc.entities.aspects.destructible :only [Destructible take-damage]]))

(defaspect Attacker
  (attack [this target world]
          {:pre [(satisfies? Destructible target)]}
          (let [damage 1]
            (take-damage target damage world))))
