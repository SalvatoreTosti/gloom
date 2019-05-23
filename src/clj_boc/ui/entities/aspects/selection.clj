(ns clj-boc.ui.entities.aspects.selection
  (:use [clj-boc.entities.core :only [defaspect]])
  (:require [lanterna.screen :as s]))

(defaspect Selection
  (up [this game]
      (let [current-UI (first (:uis game))
            new-UI (update current-UI :selection dec)]
        (assoc game :uis [new-UI])))
  (down [this game]
        (let [current-UI (first (:uis game))
              new-UI (update current-UI :selection inc)]
          (assoc game :uis [new-UI])))

  (select [this game]
          (let [current-UI (first (:uis game))
                selection (current-UI :selection)
                coll (:items this)]
            (when (and
                    (not (neg? selection))
                    (< selection (count coll)))
              (nth coll selection)))))
