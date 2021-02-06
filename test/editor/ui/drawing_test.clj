(ns editor.ui.drawing-test
  (:require [clojure.test :refer :all]
            [editor.ui.drawing :refer :all]))

(deftest add-dialog-test
  (testing "adding dialog to state"
    (are [x y z] (= z (get-in (add-dialog x y) [:editor :dialogs]))
      {} "view"  '("view")
      {:editor {:dialogs '("1")}} "2" '("2" "1")
      {:editor {:dialogs '()}} "1" '("1"))))
