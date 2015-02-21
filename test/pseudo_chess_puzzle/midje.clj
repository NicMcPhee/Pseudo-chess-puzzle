(ns pseudo_chess_puzzle.midje
  (:use [midje.sweet])
  (:require [pseudo_chess_puzzle.location_genome :as lg]))

(fact (+ 2 2) => 4)
(fact (+ 2 2) => even?)

(facts "about position-based genome"
       (fact "genome has 16 positions"
             (count (lg/initial-genome)) => 16)
       (fact "genome has 4 blanks"
             (count (filter empty? (lg/initial-genome))) => 4)
       (fact "genome has 12 non-blanks"
             (count (filter (complement empty?) (lg/initial-genome))) => 12)
       (facts "genome contains fixed pieces in correct positions"
              (get (lg/initial-genome)  2) => { :color :black :piece :B }
              (get (lg/initial-genome) 10) => { :color :white :piece :K }
              (get (lg/initial-genome) 13) => { :color :black :piece :N })
       (fact "genome contains other pieces"
             (clojure.set/subset? (set lg/unplaced-pieces) (set (lg/initial-genome)))))
