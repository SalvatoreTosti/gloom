(ns gloom.ui.quil-setup
  (:use [gloom.ui.core :only [->UI tile-size]]
        [gloom.world :only [random-world get-tile-kind get-tile-by-coord]]
        [gloom.ui.core :only [->UI push-ui pop-ui]]
        [gloom.entities.backpack :only [make-backpack]]
        [gloom.core :only [new-game]]
        [gloom.entities.player :only [make-player]])
  (:require [quil.core :as q]
            [quil.middleware :as m]))

(defn- get-start [column-number]
  (+ (* column-number tile-size) column-number))

(defn- get-tile [source-image column-number row-number]
  (let [col-start (get-start column-number)
        row-start (get-start row-number)
        img (q/create-image 16 16 :rgb)]
    (q/copy source-image img [col-start row-start 16 16] [0 0 16 16])
    img))

(defn- get-tile-row-rec [source-image row-number max-width accumulator counter]
  (let [tile-id (-> row-number
                  (* max-width)
                  (+ counter)
                  (str)
                  (keyword))
        tile (get-tile source-image counter row-number)
        accumulator (assoc accumulator tile-id tile)]
    (if
      (= counter (dec max-width)) accumulator
      (get-tile-row-rec source-image row-number max-width accumulator (inc counter)))))

(defn- get-tile-row [source-image row-number row-width]
  (get-tile-row-rec source-image row-number row-width {} 0))

(defn- get-tile-map [source-image row-count row-width]
  (->> (range row-count)
       (map #(get-tile-row source-image % row-width))
       (into {})))

(def get-tiles (memoize get-tile-map))

(defn reset-game [game]
  (let [world  (random-world)
        player (make-player world)
        player (assoc player :inventory (make-backpack))]

    (-> game
        (assoc :world world)
        (assoc-in [:world :entities :player] player)
;;         (update :world populate-world)
;;         (assoc-in [:world :entities :player :inventory] (make-backpack))
        (pop-ui)
        (push-ui (->UI :play)))))

(defn setup []
  (q/background 0)
  (q/frame-rate 15)

  (let [game  (reset-game (new-game))
        tiles (get-in game [:world :tiles])
        base-image (q/load-image "resources/monochrome.png")]
    (while (not (q/loaded? base-image))
      (println "loading base image..."))
    {:img base-image
     :tile-map (get-tiles base-image 32 32)
     :counter 0
     :game game
     :x 40
     :y 20}))
