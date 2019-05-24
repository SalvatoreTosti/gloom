(ns gloom.ui.entities.menu
  (:use [gloom.entities.core :only [Entity get-id add-aspect]]
        [gloom.ui.entities.aspects.selection :only [Selection]])
  (:require [lanterna.screen :as s]))


(defrecord Menu [id kind header items selection])

(defn make-menu
  ([kind headers items]
   (map->Menu {:id (get-id)
               :headers headers
               :kind kind
               :items items
               :selection 0}))
  ([headers items]
   (make-menu :menu headers items))
  ([items]
   (make-menu :menu nil items)))

(defn draw-string-list [screen start-offset strings]
  (dorun (for [x (range (count strings))]
           (s/put-string screen 0 (+ x start-offset) (nth strings x)))))

(defn draw-item-list [screen line-count items headers]
  (draw-string-list screen 0 headers)
  (draw-string-list screen (count headers) items))

(defn draw-menu [this game screen]
  (draw-item-list screen 0 (:items this) (:headers this))
  (s/move-cursor screen 0 (+ (count (:headers this)) (:selection this))))

(add-aspect Menu Selection)
