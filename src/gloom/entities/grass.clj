(ns gloom.entities.grass
  (:use [gloom.entities.core :only [Entity get-id add-aspect]]
        [gloom.entities.aspects.destructible :only [Destructible]]
        [gloom.entities.aspects.receiver :only [send-message-nearby]]
        [gloom.world :only [find-empty-neighbor find-empty-tile]]
        [gloom.entities.aspects.renderable :only [Renderable]]
        [gloom.entities.aspects.describable :only [Describable]]
        [gloom.entities.aspects.positionable :only [Positionable find-spawn-location]]))

(defrecord Grass [id location max-hp hp type])

(defn make-grass [location]
  (map->Grass {:id (get-id)
               :location location
               :max-hp 1
               :hp 1
               :type :grass}))

(defn should-grow []
  (< (rand) 0.0005))

(defn grow [{:keys [location]} world]
  (if-let [target (find-empty-neighbor world location)]
    (let [new-grass (make-grass target)
          world (assoc-in world [:entities (:id new-grass)] new-grass)
          world (send-message-nearby location "The grass grows." world)]
      world)
    world))

(extend-type Grass Entity
             (tick [this world]
               (if (should-grow)
                 (grow this world)
                 world)))

(defn find-adjacent-grass-tile [grasses overflow-counter world]
  (cond
    (empty? grasses) (find-empty-tile world)
    (< 100 overflow-counter) (find-empty-tile world) ;;if over 100 tries are made, just use a random empty world tile
    :else
    (let [random-grass (rand-nth grasses)
          empty-tile (find-empty-neighbor world (:location random-grass))]
      (if (nil? empty-tile)
        (find-adjacent-grass-tile grasses (inc overflow-counter) world)
        empty-tile))))

(defn place-alone? []
  (< (rand) 0.65))

(defn get-tile-position [world]
  (if (place-alone?)
    (find-empty-tile world)
    (let [grasses (->> world
                       :entities
                       vals
                       (filter #(= :grass (:type %))))]
      (find-adjacent-grass-tile grasses 0 world))))

(add-aspect Grass Destructible)
(add-aspect Grass Positionable
            (position [this world]
                      (->> world
                           (get-tile-position)
                           (assoc this :location))))
(add-aspect Grass Describable
            (type-name [this]
                       "grass"))

(add-aspect Grass Renderable
            (color [this]
                   :forest-green)
            (image [this]
                   :64))
