(ns main.main
  (:use [main.ui :only [make-sketch]])
  (:gen-class))

(defn -main []
  (make-sketch))

;; (-main)
