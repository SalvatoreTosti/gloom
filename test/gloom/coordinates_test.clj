(ns gloom.coordinates-test
  (:require [clojure.test :refer :all]
            [gloom.coordinates :refer :all]))

(deftest destination-coords-single-test
  (testing "single coordinate lookups"
    (are [x y z] (= x (destination-coords y z))
         [0,-1] [0,0] :n
         [0,1] [0,0] :s
         [1,0] [0,0] :e
         [-1,0] [0,0] :w)))

(deftest destination-coords-multi-test
  (testing "coordinate sequence lookups"
    (are [x y] (= x y)
         [[0,-1] [0,-2]] (destination-coords [0,0] :n :n)
         [[0,-1] [0,0]] (destination-coords [0,0] :n :s)
         [[0,-1] [1,-1]] (destination-coords [0,0] :n :e)
         [[0,-1] [-1,-1]] (destination-coords [0,0] :n :w))))

(deftest neighbors-test
  (testing "neighbor lookups"
    (are [x y] (= x (neighbors y))
         '([-1 0] [1 0] [0 -1] [0 1] [-1 -1] [1 -1] [-1 1] [1 1]) [0,0])))

