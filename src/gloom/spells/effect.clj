(ns gloom.spells.effect
  (:use [gloom.entities.aspects.receiver :only [send-message]]
        [gloom.entities.core :only [defaspect]]))

(defaspect Effect
  (apply-effect [this caster target world]
                world)

  (trigger [this caster target world]
           (->> world
                (apply-effect this caster target))))
