(ns gloom.ui.quil-drawing
  (:use [gloom.ui.core :only [->UI tile-size draw-tile]]
        [gloom.world :only [random-world get-tile-kind get-tile-by-coord]]
        [gloom.ui.core :only [->UI push-ui pop-ui]]
        [gloom.entities.backpack :only [make-backpack]]
        [gloom.core :only [new-game]]
        [gloom.ui.quil-setup :only [setup]]
        [gloom.ui.quil-key :only [key-pressed]]
        [gloom.ui.quil-text :only [draw-word]]
        [gloom.entities.aspects.renderable :only [color image]]
        [gloom.utils :only [enumerate]])
  (:require [quil.core :as q]
            [quil.middleware :as m]))

(def screen-size [45 24])

(defn clear-screen [tile-map]
  (let [[cols, rows] screen-size
        blank (:0 tile-map)]
    (when (q/loaded? blank)
      (doall (for [x (range cols)
                   y (range rows)]
               (q/image blank (* x tile-size) (* y tile-size)))))))

(defn get-viewport-coords [game player-location vcols vrows]
  (let [location (:location game)
        [center-x center-y] player-location
        tiles (:tiles (:world game))
        map-rows (count tiles)
        map-cols (count (first tiles))
        start-x (max 0 (- center-x (int (/ vcols 2))))
        start-y (max 0 (- center-y (int (/ vrows 2))))
        end-x (+ start-x vcols)
        end-x (min end-x map-cols)
        end-y (+ start-y vrows)
        end-y (min end-y map-rows)
        start-x (- end-x vcols)
        start-y (- end-y vrows)
        ]
    [start-x start-y end-x end-y]))


(defn tile-kind-lookup [kind]
  (cond
    (= kind :wall) :19
    (= kind :floor) :3
    :else 0))

;; (defn draw-entity [screen start-x start-y {:keys [location glyph color]}]
;;   (let [[entity-x entity-y] location
;;         x (- entity-x start-x)
;;         y (- entity-y start-y)]
;;     (s/put-string screen x y glyph {:fg color})))

(defn draw-entity [start-x start-y entity tile-map]
  (let [[x y] (:location entity)
        x (- x start-x)
        y (- y start-y)]
    (draw-tile x y (image entity) tile-map (color entity))))

(defn draw-world [vrows vcols start-x start-y end-x end-y tiles entities tile-map]
  (doseq [[vrow-idx mrow-idx] (map vector
                                   (range 0 vrows)
                                   (range start-y end-y))
          :let [row-tiles (subvec (tiles mrow-idx) start-x end-x)]]
    (doseq [vcol-idx (range vcols)
            :let [{:keys [kind glyph color]} (row-tiles vcol-idx)]]
      (let [id (tile-kind-lookup kind)]
        (draw-tile vcol-idx vrow-idx tile-map id))))

    (doseq [entity (vals entities)]
      (draw-entity start-x start-y entity tile-map))
;;     (draw-hud game)
;;     (draw-messages (:messages player))))
  )

;; (defmethod draw-ui :win [ui game])
;;   (s/put-string screen 0 0 "Congrats you win!")
;;   (s/put-string screen 0 1 "Press any escape to exit, anything else to restart..."))

;; (defmethod draw-ui :lose [ui game])
;;   (s/put-string screen 0 0 "Better luck next time")
;;   (s/put-string screen 0 1 "Press any escape to exit, anything else to restart..."))

(defn draw-string-list [strings])
;;   (dorun (for [x (range (count strings))]
;;            (s/put-string screen 0 x (nth strings x)))))

(defn draw-item-list [line-count start-draw items])
;;   (->> items
;;       (drop start-draw)
;;       (draw-string-list screen)))

(defn draw-list [ui game])
;;   (draw-item-list screen 0 0 (:list ui))
;;   (s/move-cursor screen 0 (:selection ui)))

;; (defmethod draw-ui :menu [ui game])
;;   (clear-screen)
;;   (draw-menu ui game))

;; (defmethod draw-ui :inventory [ui game])
;;   (clear-screen screen)
;;   (draw-menu ui game screen))


(defmulti draw-ui
  (fn [state ui game]
    (:kind ui)))

;; (defmethod draw-ui :start [state ui game])

(defn draw-messages [world message-duration messages tile-map]
  (let [messages (filter #(>= (+ (:tick %) message-duration) (:tick world)) messages)
        messages (map :message messages)]
    (doseq [[i msg] (enumerate messages)]
      (draw-word 0 i tile-map msg))))

(defmethod draw-ui :play [state ui game]
  (let [world (:world game)
        {:keys [tiles entities]} world
        player (:player entities)
        [cols rows] screen-size
        vcols cols
        vrows rows
        [start-x start-y end-x end-y] (get-viewport-coords game (:location player) vcols vrows)]
  (draw-world
    vrows
    vcols
    start-x
    start-y
    end-x
    end-y
    tiles
    entities
    (:tile-map state))
    (draw-messages world 2 (:messages player) (:tile-map state))
    ))

(defn draw [state]
  (let [game (get-in state [:game])
        ui (first (get-in game [:uis]))]
    (draw-ui state ui game)))

(q/defsketch example
  :title "image demo"
  :size [(* (first screen-size) tile-size) (* (second screen-size) tile-size)]
  :setup setup
  :draw draw
  :key-pressed key-pressed
  :middleware [m/fun-mode])
