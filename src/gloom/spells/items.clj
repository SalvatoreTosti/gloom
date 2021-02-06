(ns gloom.entities.spells.items
  (:use [gloom.entities.core :only [defaspect]]
        [gloom.spells.effects.delete :only [make-delete]]
        [gloom.spells.spell :only [trigger-spell make-spell]]
        [gloom.spells.effects.transfer :only [make-transfer]]))

(defaspect ItemInteraction
  (delete [this target world]
          (-> (make-spell [(make-delete [:entities])])
              (trigger-spell this target world)))
  (pick-up-it [this target world]
              (-> (make-spell [(make-transfer [:entities] [:entities (:id this) :inventory (:id target)])])
                  (trigger-spell this target world)))
  (drop-it [this target new-location world]
           (let [target (assoc target :location new-location)]
             (-> (make-spell [(make-transfer [:entities (:id this) :inventory] [:entities (:id target)])])
                 (trigger-spell this target world)))))
