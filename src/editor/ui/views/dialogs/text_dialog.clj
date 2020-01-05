(ns editor.ui.views.dialogs.text-dialog
  (:use
    [gloom.ui.core :only [draw-tile clear-box]]
    [editor.ui.views.core :only [make-view mouse->grid draw-text-relative]]))

(defn- draw-view [view state]
  (let [start-x (first (:position view))
        start-y (second (:position view))
        end-x (dec (+ start-x (:width view)))
        end-y (dec (+ start-y (:height view)))]
    (clear-box start-x start-y end-x end-y (:tile-map state))
    (doseq [x (range start-x end-x)]
            (draw-tile x start-y (:tile-map state) (:outline-id view)))
    (doseq [x (range start-x (inc end-x))]
            (draw-tile x end-y (:tile-map state) (:outline-id view)))
    (doseq [y (range start-y end-y)]
      (draw-tile start-x y (:tile-map state) (:outline-id view))
      (draw-tile end-x y (:tile-map state) (:outline-id view))
      )))

(defn- draw [view state]
  (draw-view view state)
  (draw-text-relative 1 1 view state "Text Dialog")
  (draw-text-relative 1 2 view state (:input-string view)))

(defn- on-click [[mouse-x mouse-y] view state]
  state)

(defn remove-last-character [state]
  (let [
        first-dialog (first (get-in state [:editor :dialogs]))
        first-dialog (update first-dialog :input-string #(str "" (reduce str (drop-last %))))
        other-dialogs (rest (get-in state [:editor :dialogs]))
        dialog-list (concat [first-dialog] other-dialogs)
        state (assoc-in state [:editor :dialogs] dialog-list)
        ]
    state))

(defn execute-callback [state]
  (let [first-dialog (first (get-in state [:editor :dialogs]))
        state (assoc-in state (:callback-path first-dialog) (:input-string first-dialog))]
    state))

(defn- on-input [state key-information]
  (cond
    (= 10 (:key-code key-information))
    (-> state
        execute-callback
        (update-in [:editor :dialogs] rest))

    (= 8 (:key-code key-information)) (remove-last-character state)
    (re-seq #"[a-zA-Z0-9]" (str (:raw-key key-information)))
    (let [key-str (str (:raw-key key-information))
        first-dialog (first (get-in state [:editor :dialogs]))
        first-dialog (update first-dialog :input-string #(str % key-str))
        other-dialogs (rest (get-in state [:editor :dialogs]))
        dialog-list (concat [first-dialog] other-dialogs)
        state (assoc-in state [:editor :dialogs] dialog-list)]
    state)
    :else
    state
    ))

(defn make-text-dialog
  [{:keys [position
           width
           height
           outline-id
           cursor-id
           callback-path
           ]} 
   state]
  (let [view (make-view
               {:position position
                :width width
                :height height
                :outline-id outline-id
                :cursor-id cursor-id})
        start-x (inc (first (:position view)))
        start-y (inc (second (:position view)))
        end-x (dec (dec (+ start-x (:width view))))
        end-y (dec (dec (+ start-y (:height view))))]
    (-> view
        (assoc :start [start-x start-y])
        (assoc :end [end-x end-y])

        (assoc :input-string "")
        (assoc :draw-fn draw)
        (assoc :on-click-fn on-click)
        (assoc :on-input-fn on-input)
        (assoc :callback-path callback-path))))

