(ns clj-boc.core
  (:use [clj-boc.world :only [random-world smooth-world]]
        [clj-boc.ui.drawing :only [draw-game]]
        [clj-boc.ui.input :only [get-input process-input]]
        [clj-boc.ui.core :only [->UI]]
        [clj-boc.entities.core :only [tick]])
  (:require [lanterna.screen :as s]))

(defrecord Game [world uis input])

(defn tick-entity [world entity]
  (tick entity world))

(defn tick-all [world]
  (reduce tick-entity world (vals (:entities world))))

(defn clear-messages [game]
  (assoc-in game [:world :entities :player :messages] nil))

(defn run-game [game screen]
  (loop [{:keys [input uis] :as game} game]
    (when (seq uis)
      (if (nil? input)
        (let [game (update-in game [:world] tick-all)
              _ (draw-game game screen)
              game (clear-messages game)]
              (recur (get-input game screen)))
        (recur (process-input (dissoc game :input) input))))))

(defn new-game []
  (assoc (->Game
              nil
              [(->UI :start)]
              nil)
          :location [40 20]))

(defn main
  ([screen-type] (main screen-type false))
  ([screen-type block?]
   (letfn [(go []
               (let [screen (s/get-screen screen-type)]
                 (s/in-screen screen
                              (run-game (new-game) screen))))]
     (if block?
       (go)
       (future (go))))))

(defn -main [& args]
  (let [args (set args)
        screen-type (cond
                      (args ":swing") :swing
                      (args ":text") :text
                      :else :auto)]
    (main screen-type true)))
