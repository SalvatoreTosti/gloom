(ns editor.ui.views.core-test
  (:require [clojure.test :refer :all]
            [editor.ui.views.core :refer :all]))

(deftest mouse-grid-test
  (testing "mouse grid internals"
    (are [x y z a] (= a (mouse->grid x y z))
         [0 0] 16 nil [0 0]
         [15 15] 16 nil [0 0]
         [16 16] 16 nil [1 1]
         [17 17] 16 nil [1 1])))
