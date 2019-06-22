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

;; (defn run-game [game screen]
;;   (loop [{:keys [input uis skip-tick] :as game} game]
;;     (when (seq uis)
;;       (cond
;;         (not (nil? skip-tick))
;;         (let [game (assoc-in game [:skip-tick] nil)
;;               _ (draw-game game screen)
;;               game (clear-messages game)]
;;               (recur (get-input game screen)))

;;         (nil? input)
;;         (let [game (update-in game [:world] tick-all)
;;               _ (draw-game game screen)
;;               game (clear-messages game)]
;;           (recur (get-input game screen)))

;;         :else
;;         (recur (process-input (dissoc game :input) input))))))

(defn new-game [options]
  (->Game
    nil
    [(->UI :start)]
    nil
    nil
    options))

;; (defn main
;;   ([screen-type] (main screen-type false))
;;   ([screen-type block?]
;;    (letfn [(go []
;;                (let [screen (s/get-screen screen-type)]
;;                  (s/in-screen screen
;;                               (run-game (new-game) screen))))]
;;      (if block?
;;        (go)
;;        (future (go))))))

;; (defn -main [& args]
;;   (let [args (set args)
;;         screen-type (cond
;;                       (args ":swing") :swing
;;                       (args ":text") :text
;;                       :else :auto)]
;;     (main screen-type true)))

;; (-main ":swing")
