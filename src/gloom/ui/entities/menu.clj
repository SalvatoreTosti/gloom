(ns gloom.ui.entities.menu
  (:use [gloom.entities.core :only [Entity get-id add-aspect]]
        [gloom.ui.entities.aspects.selection :only [Selection]]
        [gloom.ui.core :only [clear-screen]]
        [gloom.ui.quil-text :only [draw-text-centered text-center-start draw-text invert-word]])
  (:require [lanterna.screen :as s]))


(defrecord Menu [id kind header items selection])

(defn item-mapping [item mapping]
  [(first item) (str (get-in (second item) mapping))])

(defn pair-mapping [items mapping]
  (map #(item-mapping % mapping) items))

(defn make-menu
  ([kind header items mapping]
   (map->Menu {:id (get-id)
               :header header
               :kind kind
               :items items
               :item-pairs (pair-mapping items mapping)
               :selection 0}))
  ([header items mapping]
   (make-menu :menu header items mapping))
  ([items mapping]
   (make-menu :menu nil items mapping)))

(defn draw-string-list [start-offset tile-map strings]
  (dorun (for [x (range (count strings))]
           (draw-text 0 (+ x start-offset) tile-map (nth strings x)))))

(defn draw-menu [state {:keys [items item-pairs header selection] :as this} game]
  (clear-screen (get-in game [:options :screen-size]) (:tile-map state))
  (draw-text-centered 0 (first (get-in game [:options :screen-size])) (:tile-map state) header)
  (-> (first (get-in game [:options :screen-size]))
      (text-center-start  header)
      (invert-word 0 header))
  (draw-string-list 1 (:tile-map state) (map second item-pairs)))

(defn get-selection [{:keys [items item-pairs selection] :as this}]
  (let [selection (:selection this)
        items (:items this)]
    (when (and
            (not (neg? selection))
            (< selection (count items)))
      (-> item-pairs
          (nth selection)
          (first)))))

(add-aspect Menu Selection
            (select [this game]
              (get-selection this)))
