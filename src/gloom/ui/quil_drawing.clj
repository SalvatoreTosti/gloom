(ns quil-drawing
  (:use [gloom.ui.core :only [->UI]]
        [gloom.world :only [random-world get-tile-kind get-tile-by-coord]]
        [gloom.ui.core :only [->UI push-ui pop-ui]]
        [gloom.entities.backpack :only [make-backpack]]
        [gloom.core :only [new-game]])
  (:require [quil.core :as q]
            [quil.middleware :as m]))

(defn- get-start [column-number]
  (+ (* column-number 16) column-number))

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

(def tile-size 16)

(def screen-size [80 24])

(defn clear-screen [tile-map]
  (let [[cols, rows] screen-size
        blank (:0 tile-map)]
    (when (q/loaded? blank)
      (doall (for [x (range cols)
                   y (range rows)]
               (q/image blank (* x tile-size) (* y tile-size)))))))


(defn draw-single-tile
  ([tile-map item]
  (let [x (first (:location item))
        y (second (:location item))
        image ((:tile item) tile-map)]
     (when (q/loaded? image)
        (q/image image (* x tile-size) (* y tile-size)))))
  ([tile-map id x y]
   (let [image (id tile-map)]
     (when (q/loaded? image)
       (q/image image (* x tile-size) (* y tile-size))))))

(defn draw-tile [x y image]
  (when (q/loaded? image)
    (q/image image (* x tile-size) (* y tile-size))))

(defn draw-entity [start-x start-y tile-map {:keys [location tile]} item]
  (let [[entity-x entity-y] location
        x (- entity-x start-x)
        y (- entity-y start-y)
        image (tile tile-map)]
    (draw-image x y image)))

(defn draw-hud [game])
;;   (let [hud-row (dec (second (s/get-size screen)))
;;         player (get-in game [:world :entities :player])
;;         {:keys [location hp max-hp exp]} player
;;         [x y] location
;;         info (str "hp: [" hp "/" max-hp "]")
;;         info (str info " exp: [" exp " / " (nearest-threshold exp) "]")]
;;     (s/put-string screen 0 hud-row info)))

(defmulti draw-ui
  (fn [ui game]
    (:kind ui)))

(defmethod draw-ui :start [ui game])

(defn draw-messages [messages])
;;   (doseq [[i msg] (enumerate messages)]
;;     (s/put-string screen 0 i msg {:fg :black :bg :white})))

(defmethod draw-ui :play [ui game]
  (let [world (:world game)
        {:keys [tiles entities]} world
        player (:player entities)
        [cols rows] screen-size
        vcols cols
        vrows (dec rows)
        [start-x start-y end-x end-y] (get-viewport-coords game (:location player) vcols vrows)]
    (draw-world vrows vcols start-x start-y end-x end-y tiles)
    (doseq [entity (vals entities)]
      (draw-entity start-x start-y entity))
    (draw-hud game)
    (draw-messages (:messages player))))

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


(defn reset-game [game]
    (-> game
        (assoc :world (random-world))
;;         (update :world populate-world)
        (assoc-in [:world :entities :player :inventory] (make-backpack))
        (pop-ui)
        (push-ui (->UI :play))))

(defn tile-kind-lookup [kind]
  (cond
    (= kind :wall) :19
    (= kind :floor) :3
    :else 0))

(defn setup []
    (q/background 0)

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

(defn update-quil [state]
  (update-in state [:counter] inc))

(defn key-pressed [state key-information]
  (println key-information)
  (println (:x state) (:y state))
  (case (:key key-information)
    :w (update state :y dec)
    :a (update state :x dec)
    :s (update state :y inc)
    :d (update state :x inc)
    state))

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

(defn draw-world [vrows vcols start-x start-y end-x end-y tiles tile-map]
  (doseq [[vrow-idx mrow-idx] (map vector
                                   (range 0 vrows)
                                   (range start-y end-y))
          :let [row-tiles (subvec (tiles mrow-idx) start-x end-x)]]
    (doseq [vcol-idx (range vcols)
            :let [{:keys [kind glyph color]} (row-tiles vcol-idx)]]
      (let [id (tile-kind-lookup kind)
            img (id tile-map)]
        (draw-single-tile tile-map id vcol-idx vrow-idx)))))

(defn draw [state]
  (let [[start-x start-y end-x end-y] (get-viewport-coords (:game state) [(:x state) (:y state)] 80 24)]
  (draw-world 24 80
              start-x start-y end-x end-y
              (get-in state [:game :world :tiles])
              (:tile-map state)))
)

(q/defsketch example
  :title "image demo"
  :size [720 384]
  :setup setup
  :update update-quil
  :draw draw
  :key-pressed key-pressed
  :middleware [m/fun-mode])
