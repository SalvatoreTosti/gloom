(ns gloom.entities.aspects.receiver
  (:use [gloom.entities.core :only [defaspect]]
        [gloom.world :only [get-entities-around]]))

(defaspect Receiver
  (receive-message [this message world]
                   (let [msg {:tick (:tick world) :message message}]
                     (update-in world [:entities (:id this) :messages] conj msg))))

(defn send-message [entity message args world]
  (if (satisfies? Receiver entity)
    (receive-message entity (apply format message args) world)
    world))

(defn send-message-nearby [coord message world]
  (let [entities (get-entities-around world coord 7)
        sm (fn [world entity]
             (send-message entity message [] world))]
    (reduce sm world entities)))

