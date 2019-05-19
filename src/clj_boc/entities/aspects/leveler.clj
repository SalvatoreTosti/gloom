(ns clj-boc.entities.aspects.leveler
  (:use [clj-boc.entities.core :only [defaspect]]))

(def thresholds
  [100,
   250,
   375,
   526,
   789])

(defn nearest-threshold [n]
  (let [belows (filter #(> % n) thresholds)]
    (cond
      (> n (last thresholds)) (last thresholds)
      (empty? belows) (first thresholds)
      :else (first belows))))

(defaspect Leveler
  (level-up [{:keys [id] :as this} world]
    (let [this (update-in this [:max-hp] inc)
          this (update-in this [:hp] inc)]
      (assoc-in world [:entities id] this)))

  (add-exp [{:keys [id exp] :as this} exp-amount world]
           (let [new-exp (+ exp-amount exp)
                 new-this (assoc-in this [:exp] new-exp)]
             (if (>= new-exp (nearest-threshold exp))
               (level-up new-this world)
               (assoc-in world [:entities id] new-this)))))
