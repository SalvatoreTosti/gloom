(ns clj-boc.core
  (:use [clj-boc.world :only [random-world smooth-world]])
  (:require [lanterna.screen :as s]))

(defrecord UI [kind])
(defrecord Game [world uis input])

(def screen-size [80 24])

(defn clear-screen [screen]
  (let [[cols, rows] screen-size
         blank (apply str (repeat cols \space))]
    (doseq [row (range rows)]
      (s/put-string screen 0 row blank))))

(defn draw-world [screen vrows vcols start-x start-y end-x end-y tiles]
  (doseq [[vrow-idx mrow-idx] (map vector
                                   (range 0 vrows)
                                   (range start-y end-y))
          :let [row-tiles (subvec (tiles mrow-idx) start-x end-x)]]
    (doseq [vcol-idx (range vcols)
            :let [{:keys [glyph color]} (row-tiles vcol-idx)]]
      (s/put-string screen vcol-idx vrow-idx glyph {:fg color}))))

(defn draw-crosshairs [screen vcols vrows]
  (let [crosshair-x (int (/ vcols 2))
        crosshair-y (int (/ vrows 2))]
    (s/put-string screen crosshair-x crosshair-y "X" {:fg :red})
    (s/move-cursor screen crosshair-x crosshair-y)))

(defn get-viewport-coords [game vcols vrows]
  (let [location (:location game)
        [center-x center-y] location
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

(defmulti draw-ui
  (fn [ui game screen]
    (:kind ui)))

(defmethod draw-ui :start [ui game screen]
   (s/put-string screen 0 0 "Welcome!")
   (s/put-string screen 0 1 "Press enter to win or any key to exit..."))

(defmethod draw-ui :play [ui game screen]
  (let [world (:world game)
        tiles (:tiles world)
        [cols rows] screen-size
        vcols cols
        vrows (dec rows)
        [start-x start-y end-x end-y] (get-viewport-coords game vcols vrows)]
    (draw-world screen vrows vcols start-x start-y end-x end-y tiles)
    (draw-crosshairs screen vcols vrows)))

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


(defn move [[x y] [dx dy]]
  [(+ x dx) (+ y dy)])

(defmulti process-input
  (fn [game input]
    (:kind (last (:uis game)))))

(defmethod process-input :start [game input]
  (-> game
      (assoc :world (random-world))
      (assoc :uis [(new UI :play)])))

(defmethod process-input :play [game input]
  (case input
    :enter (assoc game :uis [(new UI :win)])
    :backspace (assoc game :uis [(new UI :lose)])
    \q (assoc game :uis [])
    \m (assoc game :world (smooth-world (:world game)))

    \w (update-in game [:location] move [0 -1])
    \a (update-in game [:location] move [-1 0])
    \s (update-in game [:location] move [0 1])
    \d (update-in game [:location] move [1 0])

    game))

(defmethod process-input :win [game input]
  (if (= input :escape)
    (assoc game :uis [])
    (assoc game :uis [(new UI :start)])))

(defmethod process-input :lose [game input]
  (if (= input :escape)
    (assoc game :uis [])
    (assoc game :uis [(new UI :start)])))

(defn get-input [game screen]
  (assoc game :input (s/get-key-blocking screen)))

(defn run-game [game screen]
  (loop [{:keys [input uis] :as game} game]
    (when-not (empty? uis)
      (draw-game game screen)
      (if (nil? input)
        (recur (get-input game screen))
        (recur (process-input (dissoc game :input) input))))))

(defn new-game []
  (assoc (new Game
              nil
              [(new UI :start)]
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
