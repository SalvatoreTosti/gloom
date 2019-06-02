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

(defn setup []
  {:img (q/load-image "resources/monochrome.png") :counter 0})

(defn update [state]
  (update-in state [:counter] inc))

(defn key-pressed [state key-information]
  (println key-information)
  state)

(defn draw [state]
  (q/background 0)
  (when (q/loaded? (:img state))
    (let [tiles (get-tiles (:img state) 32 32)
          items [{:location [4 6] :tile :10} {:location [6 3] :tile :1001}]]
      (doall (map #(draw-single-tile tiles %) items)))))

(q/defsketch example
  :title "image demo"
  :size [256 256]
  :setup setup
  :update update
  :draw draw
  :key-pressed key-pressed
  :middleware [m/fun-mode])
