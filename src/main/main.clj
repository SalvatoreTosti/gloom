(ns main.main
  (:use [main.ui :only [make-sketch]]))

(defn -main []
  (make-sketch))

(-main)
