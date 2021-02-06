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

(let [positions (vals directions)
      radius 1]
  (map #(* 2 %) [-1,1]))

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

(defn- add-vector [vec-a vec-b]
  [(+ (first vec-a) (first vec-b))
   (+ (second vec-a) (second vec-b))])

(defn line [start length direction]
  (loop [position start
         len length
         dir (dir-to-offset direction)
         accumulator []]
    (if (zero? len)
      accumulator
      (let [new-position  [(+ (first dir) (first position))
                           (+ (second dir) (second position))]]
        (recur
         new-position
         (dec len)
         dir
         (conj accumulator new-position))))))

(defn square [start radius]
  (let [upper-left (last (line start radius :nw))]
    (for [x (range (inc (* 2 radius)))
          y (range (inc (* 2 radius)))]
      (add-vector upper-left [x y]))))

(defn rectangle [start width height]
  (for [x (range width)
        y (range height)]
    (add-vector start [x y])))

(defn zed [start radius]
  (let [upper-left (line start radius :nw)]
    (for [x (range radius)
          y (range radius)]
      (add-vector start [x y]))))


