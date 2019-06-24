(ns gloom.ui.quil-setup
  (:use [gloom.ui.core :only [->UI tile-size]]
        [gloom.world :only [random-world empty-world get-tile-kind get-tile-by-coord find-empty-tile]]
        [gloom.ui.core :only [->UI push-ui pop-ui]]
        [gloom.entities.aspects.positionable :only [Positionable position]]
        [gloom.entities.backpack :only [make-backpack]]
        [gloom.entities.lichen :only [make-lichen]]
        [gloom.entities.bunny :only [make-bunny]]
        [gloom.entities.rat :only [make-rat]]
        [gloom.entities.apple :only [make-apple]]
        [gloom.entities.grass :only [make-grass]]
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

(defn add-creature [world make-creature]
  (let [creature (make-creature (find-empty-tile world))
        creature (if
                   (satisfies? Positionable creature) (position creature world)
                   creature)]
    (assoc-in world [:entities (:id creature)] creature)))

(defn add-creatures [world make-creature n]
  (nth (iterate #(add-creature % make-creature)
                world)
       n))

(defn populate-world [world]
    (-> world
        (add-creatures make-lichen 30)
        (add-creatures make-bunny 20)
        (add-creatures make-rat 20)
        (add-creatures make-apple 30)
        (add-creatures make-grass 300)
        ))

(defn reset-game [game]
  (let [world  (empty-world) ;;(random-world)
        player (make-player world)
        player (assoc player :inventory (make-backpack))
        player (assoc player :location [80 25])]

    (-> game
        (assoc :world world)
        (assoc-in [:world :tick] 0)
        (assoc-in [:world :entities :player] player)
;;         (update :world populate-world)
;;         (assoc-in [:world :entities :player :inventory] (make-backpack))
        (pop-ui)
        (push-ui (->UI :play)))))

(defn setup []
  (q/background 0)
  (q/frame-rate 15)

  (let [game  (reset-game (new-game {:screen-size [45 24]}))
        tiles (get-in game [:world :tiles])
        base-image (q/load-image "resources/monochrome.png")]
    (while (not (q/loaded? base-image))
      nil)
    {:img base-image
     :tile-map (get-tiles base-image 32 32)
     :counter 0
     :game game
     :x 40
     :y 20}))
