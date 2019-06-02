(ns quil-drawing
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

(defn draw-single-tile [tile-map item]
  (let [x (first (:location item))
        y (second (:location item))
        image ((:tile item) tile-map)]
     (when (q/loaded? image)
        (q/image image (* x 16) (* 16 y)))))

(def screen-size [80 24])

(defn clear-screen [tile-map]
  (let [[cols, rows] screen-size
        blank (:0 tile-map)]
    (when (q/loaded? blank)
      (doall (for [x (range cols)
                   y (range rows)]
               (q/image blank (* x 16) (* y 16)))))))

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

(defn setup []
  {:img (q/load-image "resources/monochrome.png") :counter 0})

(defn update-quil [state]
  (update-in state [:counter] inc))

(defn key-pressed [state key-information]
  (println key-information)
  state)

(defn draw [state]
  (q/background 0)
  (when (q/loaded? (:img state))
    (let [tiles (get-tiles (:img state) 32 32)
          items [{:location [4 6] :tile :10} {:location [6 3] :tile :1001}]]
      (clear-screen tiles)
      (doall (map #(draw-single-tile tiles %) items)))))

(q/defsketch example
  :title "image demo"
  :size [256 256]
  :setup setup
  :update update-quil
  :draw draw
  :key-pressed key-pressed
  :middleware [m/fun-mode])
