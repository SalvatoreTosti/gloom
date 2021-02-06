(ns gloom.utils)

(defn abs [i]
  (if (neg? i)
    (- i)
    i))

(defn enumerate [s]
  (map vector (iterate inc 0) s))

