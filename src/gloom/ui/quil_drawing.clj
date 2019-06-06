(ns gloom.ui.quil-drawing
  (:use [gloom.ui.core :only [->UI tile-size draw-tile]]
        [gloom.world :only [random-world get-tile-kind get-tile-by-coord]]
        [gloom.ui.core :only [->UI push-ui pop-ui]]
        [gloom.entities.backpack :only [make-backpack]]
        [gloom.core :only [new-game]]
        [gloom.ui.quil-setup :only [setup]]
        [gloom.ui.quil-key :only [key-pressed]]
        [gloom.ui.quil-text :only [draw-word]])
  (:require [quil.core :as q]
            [quil.middleware :as m]))

(def screen-size [80 24])

(defn clear-screen [tile-map]
  (let [[cols, rows] screen-size
        blank (:0 tile-map)]
    (when (q/loaded? blank)
      (doall (for [x (range cols)
                   y (range rows)]
               (q/image blank (* x tile-size) (* y tile-size)))))))

(defmulti draw-ui
  (fn [ui game]
    (:kind ui)))

(defmethod draw-ui :start [ui game])

(defn draw-messages [messages])
;;   (doseq [[i msg] (enumerate messages)]
;;     (s/put-string screen 0 i msg {:fg :black :bg :white})))

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
        start-y (- end-y vrows)]
        [start-x start-y end-x end-y]))

(defn tile-kind-lookup [kind]
  (cond
    (= kind :wall) :19
    (= kind :floor) :3
    :else 0))

(defn draw-world [vrows vcols start-x start-y end-x end-y tiles tile-map]
  (doseq [[vrow-idx mrow-idx] (map vector
                                   (range 0 vrows)
                                   (range start-y end-y))
          :let [row-tiles (subvec (tiles mrow-idx) start-x end-x)]]
    (doseq [vcol-idx (range vcols)
            :let [{:keys [kind glyph color]} (row-tiles vcol-idx)]]
      (let [id (tile-kind-lookup kind)]
        (draw-tile vcol-idx vrow-idx tile-map id)))))

;; (defmethod draw-ui :play [ui game]
;;   (let [world (:world game)
;;         {:keys [tiles entities]} world
;;         player (:player entities)
;;         [cols rows] screen-size
;;         vcols cols
;;         vrows (dec rows)
;;         [start-x start-y end-x end-y] (get-viewport-coords game (:location player) vcols vrows)]
;;     (draw-world vrows vcols start-x start-y end-x end-y tiles)
;;     (doseq [entity (vals entities)]
;;       (draw-entity start-x start-y entity))
;;     (draw-hud game)
;;     (draw-messages (:messages player))))

(defmethod draw-ui :win [ui game])
;;   (s/put-string screen 0 0 "Congrats you win!")
;;   (s/put-string screen 0 1 "Press any escape to exit, anything else to restart..."))

(defmethod draw-ui :lose [ui game])
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

(defmethod draw-ui :menu [ui game])
;;   (clear-screen)
;;   (draw-menu ui game))

(defmethod draw-ui :inventory [ui game])
;;   (clear-screen screen)
;;   (draw-menu ui game screen))

(defn draw-game [game]
  (clear-screen)
  (doseq [ui (:uis game)]
    (draw-ui ui game)))

(defn draw [state]
  (let [[start-x start-y end-x end-y] (get-viewport-coords (:game state) [(:x state) (:y state)] 80 24)]
  (draw-world 24 80
              start-x start-y end-x end-y
              (get-in state [:game :world :tiles])
              (:tile-map state)))
  (draw-word 0 0 (:tile-map state) "yeah    ok?"))

(q/defsketch example
  :title "image demo"
  :size [720 384]
  :setup setup
  :draw draw
  :key-pressed key-pressed
  :middleware [m/fun-mode])
