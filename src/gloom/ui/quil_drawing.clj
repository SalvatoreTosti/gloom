(ns gloom.ui.quil-drawing
  (:use [gloom.ui.core :only [->UI tile-size draw-tile invert-tile push-ui pop-ui peek-ui clear-row]]
        [gloom.world :only [random-world get-tile-kind get-tile-by-coord world-size]]
        [gloom.coordinates :only [destination-coords]]
        [gloom.entities.backpack :only [make-backpack]]
        [gloom.ui.quil-setup :only [setup]]
        [gloom.ui.quil-key :only [process-input]]
        [gloom.ui.quil-text :only [draw-text draw-text-centered]]
        [gloom.ui.entities.menu :only [make-menu draw-menu]]
        [gloom.entities.aspects.renderable :only [color image]]
        [gloom.utils :only [enumerate]])
  (:require [quil.core :as q]
            [quil.middleware :as m]))

(defn get-viewport-coords [screen-center {:keys [options] :as game}]
  (let [[cols rows] (:screen-size options)
        [center-x center-y] screen-center
        [map-cols map-rows] world-size
        start-x (max 0 (- center-x (int (/ cols 2))))
        start-y (max 0 (- center-y (int (/ rows 2))))
        end-x (min (+ start-x cols) map-cols)
        end-y (min (+ start-y rows) map-rows)]
    [(- end-x cols) (- end-y rows) end-x end-y]))

(defn draw-wall [location world]
  (let [north (= :wall (get-tile-kind world (destination-coords location :n)))
        east (= :wall (get-tile-kind world (destination-coords location :e)))
        south (= :wall (get-tile-kind world (destination-coords location :s)))
        west (= :wall (get-tile-kind world (destination-coords location :w)))]
    (cond
      (and
       (not north)
       (not east)
       (not south)
       (not west)) :51
      (and
       (not north)
       (not east)) :20
      (and
       (not east)
       (not south)) :84
      (and
       (not south)
       (not west)) :82
      (and
       (not west)
       (not north)) :18
      (not north) :19
      (not east) :52
      (not south) :83
      (not west) :50
      :else :51)))

(defn tile-kind-lookup [kind location world]
  (cond
    (= kind :wall) (draw-wall location world)
    (= kind :floor) :0
    :else 0))

(defn draw-entity [start-x start-y entity tile-map]
  (let [[x y] (:location entity)
        x (- x start-x)
        y (- y start-y)]
    (draw-tile x y tile-map (image entity) (color entity))))

(defn draw-entities [viewport-coordinates entities tile-map]
  (let [[start-x start-y] viewport-coordinates]
    (doseq [entity entities]
      (draw-entity start-x start-y entity tile-map))))

(defn draw-terrain [screen-size viewport-coordinates tiles tile-map world]
  (let [[view-cols view-rows] screen-size
        [start-x start-y end-x end-y] viewport-coordinates]
    (doseq [[vrow-idx mrow-idx] (map vector
                                     (range view-rows)
                                     (range start-y end-y))
            :let [row-tiles (subvec (tiles mrow-idx) start-x end-x)]]
      (doseq [col (range view-cols)
              :let [{:keys [kind color]} (row-tiles col)]]
        (draw-tile col vrow-idx tile-map (tile-kind-lookup kind [(+ start-x col) (+ start-y vrow-idx)] world) color)))))

(defn draw-world [screen-size viewport-coordinates {:keys [tiles entities] :as world} tile-map]
  (draw-terrain screen-size viewport-coordinates tiles tile-map world)
  (draw-entities viewport-coordinates (vals entities) tile-map))

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
      (draw-text 0 i tile-map msg))))

(defn draw-hud [{:keys [world options] :as game} tile-map]
  (let [screen-size (:screen-size options)
        y (dec (second screen-size))
        player (get-in world [:entities :player])
        display-word (str "health: " (:hp player) "/" (:max-hp player) " exp: " (:exp player))]
    (clear-row y screen-size tile-map)
    (draw-text 0 y tile-map display-word)))

(defmethod draw-ui :play [state ui game]
  (let [world (:world game)
        player (get-in world [:entities :player])]
    (draw-world
     (get-in game [:options :screen-size])
     (get-viewport-coords (:location player) game)
     world
     (:tile-map state))
    (draw-messages world 2 (:messages player) (:tile-map state))
    (draw-hud game (:tile-map state))))

(defmethod draw-ui :menu [state ui game]
  (draw-menu state ui game))

(defn draw-cursor [viewport-coordinates state]
  (let [ui (peek-ui (:game state))
        [x y] (:location ui)
        [start-x start-y] viewport-coordinates
        x (- x start-x)
        y (- y start-y)]
    (invert-tile x y)))

(defmethod draw-ui :cursor [state ui {:keys [world options] :as game}]
  (let [player (get-in world [:entities :player])
        viewport-coordinates (get-viewport-coords (:location player) game)]
    (draw-world
     (:screen-size options)
     viewport-coordinates
     world
     (:tile-map state))
    (draw-cursor viewport-coordinates state)))

(defn draw-game [state]
  (draw-ui
   state
   (peek-ui (:game state))
   (:game state)))

(defn make-sketch []
  (q/defsketch gloom-sketch
    :title "gloom"
    :size (let [screen-size [45 24]]
            [(* (first  screen-size) tile-size)
             (* (second screen-size) tile-size)])
    :setup setup
    :draw draw-game
    :key-pressed process-input
    :middleware [m/fun-mode]))
