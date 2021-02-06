(ns gloom.spells.spell
  (:use [gloom.entities.aspects.receiver :only [send-message]]
        [gloom.spells.effects.effect :only [trigger]]
        [gloom.entities.core :only [get-id add-aspect defaspect]]))

(defrecord Spell [id name effects])

(defn make-spell [effects]
  (map->Spell {:id (get-id)
               :name "damage"
               :effects effects}))

(defn apply-effects [effects caster target world]
  (if
   (empty? effects) world
   (let [effect (first effects)
         world  (trigger effect caster target world)]
     (apply-effects (rest effects) caster target world))))

(defaspect SpellContainer
  (get-effects [this]
               (:effects this))

  (trigger-spell [this caster target world]
                 (let [effects (get-effects this)]
                   (apply-effects effects caster target world))))

(add-aspect Spell SpellContainer)
