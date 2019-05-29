(ns gloom.ui.entities.menu
  (:use [gloom.entities.core :only [Entity get-id add-aspect]]
        [gloom.ui.entities.aspects.selection :only [Selection]])
  (:require [lanterna.screen :as s]))


(defrecord Menu [id kind header items selection])

(defn item-mapping [item mapping]
  [(first item) (str (get-in (second item) mapping))])

(defn pair-mapping [items mapping]
  (map #(item-mapping % mapping) items))

(defn make-menu
  ([kind headers items mapping]
   (map->Menu {:id (get-id)
               :headers headers
               :kind kind
               :items items
               :item-pairs (pair-mapping items mapping)
               :selection 0}))
  ([headers items mapping]
   (make-menu :menu headers items mapping))
  ([items mapping]
   (make-menu :menu nil items mapping)))

(defn draw-string-list [screen start-offset strings]
  (dorun (for [x (range (count strings))]
           (s/put-string screen 0 (+ x start-offset) (nth strings x)))))

(defn draw-item-list [screen line-count items headers]
  (draw-string-list screen 0 headers)
  (draw-string-list screen (count headers) items))

(defn draw-menu [{:keys [items item-pairs headers selection] :as this} game screen]
    (draw-item-list screen 0 (map second item-pairs) headers)
    (s/move-cursor screen 0 (+ (count headers) selection)))

(defn get-selection [{:keys [items item-pairs headers selection] :as this}]
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
