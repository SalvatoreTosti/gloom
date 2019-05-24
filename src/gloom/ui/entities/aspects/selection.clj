(ns gloom.ui.entities.aspects.selection
  (:use [gloom.entities.core :only [defaspect]])
  (:require [lanterna.screen :as s]))

(defaspect Selection
  (up [this game]
      (let [current-UI (first (:uis game))
            new-UI (update current-UI :selection dec)]
        (if (pos? (:selection current-UI))
          (assoc game :uis [new-UI])
          game)))

  (down [this game]
        (let [current-UI (first (:uis game))
              new-UI (update current-UI :selection inc)]
          (if (< (:selection current-UI) (dec (count (:items current-UI))))
            (assoc game :uis [new-UI])
            game)))

  (select [this game]
          (let [current-UI (first (:uis game))
                selection (:selection current-UI)
                items (:items this)]
            (when (and
                    (not (neg? selection))
                    (< selection (count items)))
              (nth items selection)))))
