(ns gloom.entities.aspects.renderable
  (:use [gloom.entities.core :only [defaspect]]))

(defaspect Renderable
  (color [this]
         :white)
  (image [this]
         :821))
