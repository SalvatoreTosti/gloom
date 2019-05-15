(ns clj-boc.ui.drawing
  (:use [clj-boc.utils :only [enumerate]]
        [clj-boc.entities.aspects.leveler :only [nearest-threshold]])
  (:require [lanterna.screen :as s]))

(def screen-size [80 24])

(defn clear-screen [screen]
  (let [[cols, rows] screen-size
         blank (apply str (repeat cols \space))]
    (doseq [row (range rows)]
      (s/put-string screen 0 row blank))))

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

(defn draw-entity [screen start-x start-y {:keys [location glyph color]}]
  (let [[entity-x entity-y] location
        x (- entity-x start-x)
        y (- entity-y start-y)]
    (s/put-string screen x y glyph {:fg color})))

(defn highlight-player [screen start-x start-y player]
  (let [[player-x player-y] (:location player)
        x (- player-x start-x)
        y (- player-y start-y)]
    (s/move-cursor screen x y)))

(defn draw-world [screen vrows vcols start-x start-y end-x end-y tiles]
  (doseq [[vrow-idx mrow-idx] (map vector
                                   (range 0 vrows)
                                   (range start-y end-y))
          :let [row-tiles (subvec (tiles mrow-idx) start-x end-x)]]
    (doseq [vcol-idx (range vcols)
            :let [{:keys [glyph color]} (row-tiles vcol-idx)]]
      (s/put-string screen vcol-idx vrow-idx glyph {:fg color}))))

(defn draw-hud [screen game]
  (let [hud-row (dec (second (s/get-size screen)))
        player (get-in game [:world :entities :player])
        {:keys [location hp max-hp exp]} player
        [x y] location
        info (str "hp: [" hp "/" max-hp "]")
        info (str info " exp: [" exp " / " (nearest-threshold exp) "]")]
    (s/put-string screen 0 hud-row info)))

(defmulti draw-ui
  (fn [ui game screen]
    (:kind ui)))

(defmethod draw-ui :start [ui game screen]
   (s/put-string screen 0 0 "Welcome!")
   (s/put-string screen 0 1 "Press enter to win or any key to exit..."))

(defn draw-messages [screen messages]
  (doseq [[i msg] (enumerate messages)]
    (s/put-string screen 0 i msg {:fg :black :bg :white})))

(defmethod draw-ui :play [ui game screen]
  (let [world (:world game)
        {:keys [tiles entities]} world
        player (:player entities)
        [cols rows] screen-size
        vcols cols
        vrows (dec rows)
        [start-x start-y end-x end-y] (get-viewport-coords game (:location player) vcols vrows)]
    (draw-world screen vrows vcols start-x start-y end-x end-y tiles)
    (doseq [entity (vals entities)]
      (draw-entity screen start-x start-y entity))
    (draw-hud screen game)
    (draw-messages screen (:messages player))
    (highlight-player screen start-x start-y player)))

(defmethod draw-ui :win [ui game screen]
  (s/put-string screen 0 0 "Congrats you win!")
  (s/put-string screen 0 1 "Press any escape to exit, anything else to restart..."))

(defmethod draw-ui :lose [ui game screen]
  (s/put-string screen 0 0 "Better luck next time")
  (s/put-string screen 0 1 "Press any escape to exit, anything else to restart..."))

(defn draw-game [game screen]
  (clear-screen screen)
  (doseq [ui (:uis game)]
    (draw-ui ui game screen))
  (s/redraw screen))
