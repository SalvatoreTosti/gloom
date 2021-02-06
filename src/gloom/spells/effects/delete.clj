(ns gloom.spells.effects.delete
  (:use [gloom.spells.effects.effect :only [Effect]]
        [gloom.entities.core :only [get-id add-aspect]]))

(defrecord Delete [id name world-relative-location])

(defn make-delete [location]
  (map->Delete {:id (get-id)
                :name "delete"
                :world-relative-location location}))
(add-aspect Delete Effect
            (apply-effect [this caster target world]
                          (-> world
                              (update-in (:world-relative-location this) dissoc (:id target)))))
                              ;;[:entities (:id target)]
