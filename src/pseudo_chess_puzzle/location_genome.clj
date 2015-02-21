(ns pseudo_chess_puzzle.location_genome)

(def placed-pieces
  [ { :color :black :piece :B }
    { :color :white :piece :K }
    { :color :black :piece :N } ])

(def all-pieces
  (for [color [:white :black]
        piece [:K :Q :R :B :N :P]]
    { :color color :piece piece }))

(def unplaced-pieces
  (remove #(some (partial = %) placed-pieces) all-pieces))

(defn place-piece-randomly [board piece]
  (let [blank-positions (filter #(= {} (get board %)) (range 16))
        new-pos (nth blank-positions (rand-int (count blank-positions)))]
    (assoc board new-pos piece)))

(defn initial-genome []
  (let [start (assoc (vec (repeat 16 {}))
                 2 { :color :black :piece :B }
                10 { :color :white :piece :K }
                13 { :color :black :piece :N })]
    (reduce place-piece-randomly start unplaced-pieces)))
