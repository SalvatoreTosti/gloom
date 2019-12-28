(ns editor.ui.input
   (:use
     [editor.ui.views.core :only [coordinates-in-view?]]
     [editor.ui.views.canvas :only [pickle-canvas-view unpickle-canvas-view]]
     [editor.ui.views.entity-builder :only [pickle-entity-builder-view unpickle-entity-builder-view]]
     [editor.ui.views.grid :only [pickle-grid-view unpickle-grid-view]])


  (:require [quil.core :as q]))

(defn pickle [view]
  (case (:kind view)
    :canvas (pickle-canvas-view view)
    :entity-builder (pickle-entity-builder-view view)
    :grid (pickle-grid-view view)
    view))

(defn unpickle [view]
  (case (:kind view)
    :canvas (unpickle-canvas-view view)
    :entity-builder (unpickle-entity-builder-view view)
    :grid (unpickle-grid-view view)
    view))

;; (defn write-file-views [filename views]
;;   (spit filename views))

;; (set! *default-data-reader-fn* tagged-literal)


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
      :s (do
           (->>
             (get-in state [:editor :views])
             vals
             (map pickle)
             (prn-str)
             (spit "test.txt")
             )
           ;update state with "written out" message
           state)
      :l (->>
           (slurp "test.txt")
           (read-string)
           (map unpickle)
           (map #(vector (:id %) %))
           (into {})
           ;;Add look up for canvas's palette picker, right now it doesn't draw again after loading
           (assoc-in state [:editor :views]))
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
