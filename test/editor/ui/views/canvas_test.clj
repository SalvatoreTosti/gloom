(ns editor.ui.views.canvas-test
  (:require [clojure.test :refer :all]
            [editor.ui.views.canvas :refer :all]))

(defn default-view []
  {:position [0 0]
   :width 100
   :height 100
   :outline-id :119
   :cursor-id :787})

(deftest make-canvas-view-base-test
  (testing "make canvas view base"
    (are [x y z] (= z (y (make-canvas-view (default-view) x)))
         {} :kind :canvas
         {} :position [0 0]
         {} :width 100
         {} :height 100
         {} :outline-id :119
         {} :cursor-id :787
         {} :start [1 1]
         {} :end [99 99])))

(deftest make-canvas-view-canvas-test
  (testing "make canvas view canvas"
    (-> (default-view)
        (make-canvas-view  {})
        (:canvas)
        (count)
        (= (* 98 98))
        is)))

(deftest pickle-canvas-view-test
  (testing "pickle canvas view"
    (are [x y]
         (= y (x (pickle-canvas-view (make-canvas-view (default-view) {}))))
         :kind :canvas
         :position [0 0]
         :width 100
         :height 100
         :outline-id :119
         :cursor-id :787
         :pixel-coordinates {:start [0 0], :end [1600 1600]}
         :palette-view-id nil)))

(deftest unpickle-canvas-view-test
  (testing "unpickle canvas view"
    (are [x y]
         (= y (x (unpickle-canvas-view (pickle-canvas-view (make-canvas-view (default-view) {})))))
         :kind :canvas
         :position [0 0]
         :width 100
         :height 100
         :outline-id :119
         :cursor-id :787
         :pixel-coordinates {:start [0 0], :end [1600 1600]}
         :palette-view-id nil)))
