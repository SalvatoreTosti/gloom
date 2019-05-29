(ns gloom.ui.entities.aspects.selection
  (:use [gloom.entities.core :only [defaspect]]
        [gloom.ui.core :only [push-ui pop-ui]])
  (:require [lanterna.screen :as s]))

(defaspect Selection
  (up [this game]
      (let [new-UI (update this :selection dec)]
        (if (pos? (:selection this))
          (-> game
              (pop-ui)
              (push-ui new-UI))
          game)))

  (down [this game]
        (let [new-UI (update this :selection inc)]
          (if (< (:selection this) (dec (count (:items this))))
            (-> game
                (pop-ui)
                (push-ui new-UI))
            game)))

  (select [this game]
          (let [selection (:selection this)
                items (:items this)]
            (when (and
                    (not (neg? selection))
                    (< selection (count items)))
              (nth items selection)))))
