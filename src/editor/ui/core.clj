(ns editor.ui.core
  (:use
    [gloom.ui.core :only [tile-size]]
    )
   (:require [quil.core :as q]))

(def ids (ref 0))

(defn get-id []
  (dosync
    (let [id @ids]
      (alter ids inc)
      id)))
