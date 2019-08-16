(ns editor.ui.input
   (:use
     [editor.ui.core :only [coordinates-in-view?]])
  (:require [quil.core :as q]))

(defn process-input-editor [state key-information]
   (case (:key key-information)
      :q (-> state
             (dissoc :editor)
             (assoc :mode :start))
     state))

(defn on-click [state]
  (let [coordinates [(q/mouse-x) (q/mouse-y)]
        views (get-in state [:editor :views])]
  (filter #(coordinates-in-view? coordinates %) views)))
