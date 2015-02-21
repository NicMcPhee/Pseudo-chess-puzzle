(ns pseudo_chess_puzzle.location_genome)

(defn position-string-representation [piece]
  (reduce #(str %1 (get (name %2) 0)) "" (vals piece)))

(defn share-row? [board x y]
  (let [x-pos (.indexOf board x)
        y-pos (.indexOf board y)]
    (= (quot x-pos 4) (quot y-pos 4))))

(defn share-col? [board x y]
  (let [x-pos (.indexOf board x)
        y-pos (.indexOf board y)]
    (= (rem x-pos 4) (rem y-pos 4))))

(defn share-diag? [board x y]
  (let [x-pos (.indexOf board x)
        y-pos (.indexOf board y)
        diff (- x-pos y-pos)]
    (or (zero? (rem diff 5))
        (and (zero? (rem diff 3))
             (= (> x-pos y-pos)
                (< (rem x-pos 4) (rem y-pos 4)))))))

(defn share-row-col-diag? [board x y]
  (or (share-row? board x y)
      (share-col? board x y)
      (share-diag? board x y)))

(defn adjacent? [board x y]
  (let [x-pos (.indexOf board x)
        y-pos (.indexOf board y)]
    (and (>= 1 (Math/abs (- (rem x-pos 4) (rem y-pos 4))))
         (>= 1 (Math/abs (- (quot x-pos 4) (quot y-pos 4)))))))

(defn two-apart? [board x y]
  (let [x-pos (.indexOf board x)
        x-row (quot x-pos 4)
        x-col (rem x-pos 4)
        y-pos (.indexOf board y)
        y-row (quot y-pos 4)
        y-col (rem y-pos 4)]
    (or (and (= x-row y-row)
             (= 3 (Math/abs (- x-col y-col))))
        (and (= x-col y-col)
             (= 3 (Math/abs (- x-row y-row)))))))

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
