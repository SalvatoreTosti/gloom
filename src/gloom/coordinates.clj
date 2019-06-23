(ns gloom.coordinates)

(def directions
  {:w  [-1 0]
   :e  [1 0]
   :n  [0 -1]
   :s  [0 1]
   :nw [-1 -1]
   :ne [1 -1]
   :sw [-1 1]
   :se [1 1]})

(defn- offset-coords
  "Offset the starting coordinate by the given amount, returning the result coordinate."
  [[x y] [dx dy]]
  [(+ x dx) (+ y dy)])

(defn- dir-to-offset
  "Convert a direction to the offset for moving 1 in that direction."
  [dir]
  (directions dir))

(defn- destination-coords-rec [location dirs accumulator]
  (if (empty? dirs) accumulator
    (let [coord (offset-coords location (dir-to-offset (first dirs)))]
      (destination-coords-rec coord (rest dirs) (conj accumulator coord)))))

(defn destination-coords
  [origin & args]
  (let [coordinates (destination-coords-rec origin args [])]
    (if (= 1 (count coordinates)) (first coordinates)
      coordinates)))

(defn neighbors
  [origin]
  (map offset-coords (vals directions) (repeat origin)))
