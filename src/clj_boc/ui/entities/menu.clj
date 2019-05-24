(ns clj-boc.ui.entities.menu
  (:use [clj-boc.entities.core :only [Entity get-id add-aspect]]
        [clj-boc.ui.entities.aspects.selection :only [Selection]])
  (:require [lanterna.screen :as s]))


(defrecord Menu [id kind items selection])

(defn make-menu [kind items]
  (map->Menu {:id (get-id)
              :kind kind
              :items items
              :selection 0}))

(defn draw-string-list [screen strings]
  (dorun (for [x (range (count strings))]
           (s/put-string screen 0 x (nth strings x)))))

(defn draw-item-list [screen line-count start-draw items]
  (->> items
      (drop start-draw)
      (draw-string-list screen)))

(defn draw-menu [this game screen]
  (draw-item-list screen 0 0 (:items this))
  (s/move-cursor screen 0 (:selection this)))

(add-aspect Menu Selection)
