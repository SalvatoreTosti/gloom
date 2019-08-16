(ns editor.ui.core)

(def ids (ref 0))

(defn get-id []
  (dosync
    (let [id @ids]
      (alter ids inc)
      id)))

(defn- coordinates-between? [[x y] [start-x start-y] [end-x end-y]]
  (and
    (>= x start-x)
    (<= x end-x)
    (>= y start-y)
    (<= y end-y)))

(defn coordinates-in-view? [coordinates view]
  (coordinates-between?
    coordinates
    (get-in view [:pixel-coordinates :start])
    (get-in view [:pixel-coordinates :end])))
