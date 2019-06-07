(ns gloom.ui.quil-text
  (:use [gloom.ui.core :only [draw-tile]])
  (:require [quil.core :as q]))

(defn character-to-id [id]
  (let [id (clojure.string/lower-case id)]
  (case id
    "a" :979
    "b" :980
    "c" :981
    "d" :982
    "e" :983
    "f" :984
    "g" :985
    "h" :986
    "i" :987
    "j" :988
    "k" :989
    "l" :990
    "m" :991
    "n" :1011
    "o" :1012
    "p" :1013
    "q" :1014
    "r" :1015
    "s" :1016
    "t" :1017
    "u" :1018
    "v" :1019
    "w" :1020
    "x" :1021
    "y" :1022
    "z" :1023

    "0" :947
    "1" :948
    "2" :949
    "3" :950
    "4" :951
    "5" :952
    "6" :953
    "7" :954
    "8" :955
    "9" :956
    ":" :957
    "." :958
    "%" :959

    "?" :821
    " " :0
    :821)))

(defn- draw-word-rec [x y tile-map ids]
  (when (not (empty? ids))
    (do
      (draw-tile x y tile-map (first ids))
      (draw-word-rec (inc x) y tile-map (rest ids)))))

(defn draw-word [x y tile-map word]
  (->> word
       (map str)
       (map character-to-id)
       (draw-word-rec x y tile-map)))
