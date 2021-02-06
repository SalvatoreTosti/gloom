(ns gloom.world
  (:use [gloom.coordinates :only [rectangle square neighbors destination-coords]]
        [gloom.utils :only [abs]]))

(def world-size [160 50])

(defrecord World [tiles size])
(defrecord Tile [kind color])

(defn make-world [tiles size]
  (map->World {:tiles tiles
               :size size}))

(def tiles
  {:floor (->Tile :floor :light-gray)
   :wall  (->Tile :wall :dark-gray)
   :bound (->Tile :bound :black)})

(defn get-tile-from-tiles [tiles [x y]]
  (get-in tiles [y x] (:bound tiles)))

(defn random-coordinate []
  (let [[cols rows] world-size]
    [(rand-int cols) (rand-int rows)]))

(defn random-tiles [world-size]
  (let [[cols rows] world-size]
    (letfn [(random-tile []
              (tiles (rand-nth [:floor :wall])))
            (random-row []
              (vec (repeatedly cols random-tile)))]
      (vec (repeatedly rows random-row)))))

(defn empty-tiles [world-size]
  (let [[cols rows] world-size]
    (letfn [(random-tile []
              (tiles (rand-nth [:floor])))
            (random-row []
              (vec (repeatedly cols random-tile)))]
      (vec (repeatedly rows random-row)))))

;; (empty-tiles)

(defn get-smoothed-tile [block]
  (let [tile-counts (frequencies (map :kind block))
        floor-threshold 5
        floor-count (get tile-counts :floor 0)
        result (if (>= floor-count floor-threshold)
                 :floor
                 :wall)]
    (tiles result)))

(defn block-coords [x y]
  (for [dx [-1 0 1]
        dy [-1 0 1]]
    [(+ x dx) (+ y dy)]))

(defn get-block [tiles x y]
  (map (partial get-tile-from-tiles tiles)
       (block-coords x y)))

(defn get-smoothed-row [tiles y]
  (mapv (fn [x]
          (get-smoothed-tile (get-block tiles x y)))
        (range (count (first tiles)))))

(defn get-smoothed-tiles [tiles]
  (mapv (fn [y]
          (get-smoothed-row tiles y))
        (range (count tiles))))

(defn smooth-world [{:keys [tiles] :as world}]
  (assoc world :tiles (get-smoothed-tiles tiles)))

(defn smooth-world [{:keys [tiles] :as world}]
  (assoc world :tiles (get-smoothed-tiles tiles)))

(defn get-tile [world coord]
  (get-tile-from-tiles (:tiles world) coord))

(defn get-tile-by-coord [world coord]
  (get-tile-from-tiles (:tiles world) coord))

(defn get-tile-kind [world coord]
  (:kind (get-tile world coord)))

(defn set-tile [world [x y] tile]
  (assoc-in world [:tiles y x] tile))

(defn set-tile-floor [world coord]
  (set-tile world coord (:floor tiles)))

(defn get-entity-at [world coord]
  (first (filter #(= coord (:location %))
                 (vals (:entities world)))))

(defn is-empty? [world coord]
  (and (#{:floor} (get-tile-kind world coord))
       (not (get-entity-at world coord))))

(defn find-empty-tile [world]
  (loop [coord (random-coordinate)]
    (if (is-empty? world coord)
      coord
      (recur (random-coordinate)))))

(defn find-empty-neighbor [world coord]
  (let [candidates (filter #(is-empty? world %) (neighbors coord))]
    (when (seq candidates)
      (rand-nth candidates))))

(defn check-tile
  [world dest pred]
  (pred (get-tile-kind world dest)))

(defn radial-distance
  [[x1 y1] [x2 y2]]
  (max (abs (- x1 x2))
       (abs (- y1 y2))))

(defn get-entities-around
  ([world coord] (get-entities-around world coord 1))
  ([world coord radius]
   (filter #(<= (radial-distance coord (:location %))
                radius)
           (vals (:entities world)))))

(defn get-tiles-around
  ([world coord] (get-tiles-around world coord 1))
  ([world coord radius]
   (map
    #(get-tile-from-tiles (:tiles world) %)
    (square coord radius))))

(defn set-tiles [world locations tile-kind]
  (if (empty? locations)
    world
    (let [location (first locations)
          kind (tile-kind tiles)
          world (set-tile world location kind)]
      (set-tiles world (rest locations) tile-kind))))

(defn spawn-room [world location size wall-width]
  (-> world
      (set-tiles (square location (+ size wall-width)) :wall)
      (set-tiles (square location size) :floor)
      (set-tiles (rectangle location 1 (inc (+ size wall-width))) :floor)))

(defn random-world
  ([]
   (random-world [160 50]))
  ([size]
   (let [world (make-world (random-tiles size) size)
         world (nth (iterate smooth-world world) 3)]
     world)))

(defn empty-world
  ([]
   (empty-world [160 50]))
  ([size]
   (make-world (empty-tiles size) size)))
