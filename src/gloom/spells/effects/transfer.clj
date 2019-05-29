(ns gloom.spells.effects.transfer
  (:use [gloom.spells.effects.effect :only [Effect]]
        [gloom.entities.core :only [get-id add-aspect]]))

(defrecord Transfer [id name start-world-relative-location end-world-relative-location])

(defn make-transfer [start-location end-location]
  (map->Transfer{:id (get-id)
               :name "transfer"
               :start-world-relative-location start-location
               :end-world-relative-location end-location}))

(add-aspect Transfer Effect
            (apply-effect [this caster target world]
                          (let [start-location (:start-world-relative-location this)
                                end-location (:end-world-relative-location this)]
                          (-> world
                              (update-in start-location dissoc (:id target))
                              (assoc-in end-location target)))))
