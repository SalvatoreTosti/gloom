(ns gloom.entities.rat
  (:use [gloom.entities.core :only [Entity get-id add-aspect]]
        [gloom.entities.aspects.mobile :only [Mobile move can-move?]]
        [gloom.entities.aspects.destructible :only [Destructible]]
        [gloom.world :only [find-empty-tile find-empty-neighbor get-entities-around]]
        [gloom.entities.aspects.renderable :only [Renderable]]
        [gloom.entities.aspects.describable :only [Describable]]))

(defrecord Rat [id color location max-hp hp name])

(defn make-rat [location]
  (map->Rat {:id (get-id)
             :color :yellow
             :location location
             :max-hp 5
             :hp 5
             :name "rat"}))

(defn should-move [this world]
  (let [radius 5
        player-near (->> (get-entities-around world (:location this) radius)
                         (filter #(= (:id %) :player))
                         empty?
                         not)]
    (if player-near
      true
      (< (rand) 0.1))))

(defn get-move-location [rat world]
  (if-let [target (find-empty-neighbor world (:location rat))]
    target
    (:location rat)))

(extend-type Rat Entity
             (tick [this world]
               (let [move-location (get-move-location this world)]
                 (cond
                   (not (should-move this world)) world
                   (not (can-move? this move-location world)) world
                   :else (move this (get-move-location this world) world)))))

(add-aspect Rat Mobile)
(add-aspect Rat Destructible)
(add-aspect Rat Describable
            (type-name [this]
                       "rat"))

(add-aspect Rat Renderable
            (color [this]
                   :dark-gray)
            (image [this]
                   :287))

