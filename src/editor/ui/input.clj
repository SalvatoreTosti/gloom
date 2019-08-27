(ns editor.ui.input
   (:use
     [editor.ui.views.core :only [coordinates-in-view?]])
  (:require [quil.core :as q]))

(defn process-input-editor [state key-information]
  (let [top-dialog (-> state
                       (get-in [:editor :dialogs])
                       last)
        on-input-fn (:on-input-fn top-dialog)]
  (if top-dialog
    (on-input-fn state key-information)
    (case (:key key-information)
      :q (-> state
             (dissoc :editor)
             (assoc :mode :start))
     state))))

(defn get-clicked-view [state]
  (->> (get-in state [:editor :views])
       vals
       (filter #(coordinates-in-view? [(q/mouse-x) (q/mouse-y)] %))
       first))

(defn update-editor [state]
   (if (q/mouse-button)
    (let [clicked-view (get-clicked-view state)
          on-click-fn (:on-click-fn clicked-view)]
      (if on-click-fn
        (on-click-fn
          [(q/mouse-x) (q/mouse-y)]
          clicked-view
          state)
        state))
     state))
