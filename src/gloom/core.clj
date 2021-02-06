(ns gloom.core
  (:use [gloom.world :only [random-world smooth-world]]
        [gloom.ui.core :only [->UI]]
        [gloom.entities.core :only [tick]])
  (:require [lanterna.screen :as s]))

(defrecord Game [world uis input tick-skip options])

(defn tick-entity [world entity]
  (tick entity world))

(defn tick-all [world]
  (reduce tick-entity world (vals (:entities world))))

(defn clear-messages [game]
  (assoc-in game [:world :entities :player :messages] nil))

(defn new-game [options]
  (->Game
   nil
   [(->UI :start)]
   nil
   nil
   options))
