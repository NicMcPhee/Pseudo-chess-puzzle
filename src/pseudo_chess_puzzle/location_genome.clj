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
    (or (and (zero? (rem diff 5))
             (= (> x-pos y-pos)
                (> (rem x-pos 4) (rem y-pos 4))))
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

(defn adjacent-pieces [board p]
  (let [pos (.indexOf board p)
        row (quot pos 4)
        col (rem pos 4)]
    (for [r (range (max 0 (dec row)) (inc (min 3 (inc row))))
          c (range (max 0 (dec col)) (inc (min 3 (inc col))))
          :when (not (and (= r row) (= c col)))
          :let [piece (nth board (+ (* 4 r) c))]
          :when (not= {} piece)]
      piece)))

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

(defn parse-piece-string [piece-string]
  (if (= "sp" piece-string)
    {}
    { :color (if (= (get piece-string 0) \b) :black :white)
      :piece (keyword (subs piece-string 1)) }))

(defn initial-pieces-correct? [board]
  (and (= (nth board 2) { :color :black :piece :B })
       (= (nth board 10) { :color :white :piece :K })
       (= (nth board 13) { :color :black :piece :N })))

(defn error-vector [board]
  { :1-orig-placement (initial-pieces-correct? board),
    :2-Ks-not-share (not (share-row-col-diag? board (parse-piece-string "bK") (parse-piece-string "wK"))),
    :3-Bs-not-share (not (share-row-col-diag? board (parse-piece-string "bB") (parse-piece-string "wB"))),
    :4-Ns-not-adjacent (not (adjacent? board (parse-piece-string "bN") (parse-piece-string "wN"))),
    :5-Qs-share-diag (share-diag? board (parse-piece-string "bQ") (parse-piece-string "wQ")),
    :6.1-Rs-two-spaces (two-apart? board (parse-piece-string "bR") (parse-piece-string "wR")),
    :6.2-bR-adjacent-one (= 1 (count (adjacent-pieces board (parse-piece-string "bR")))),
    :6.3-wR-adjacent-one (= 1 (count (adjacent-pieces board (parse-piece-string "wR")))),
    :6.4-bR-adjacent-black (every? #(= :black (:color %)) (adjacent-pieces board (parse-piece-string "bR"))),
    :6.5-wR-adjacent-white (every? #(= :white (:color %)) (adjacent-pieces board (parse-piece-string "wR"))),
    :7.1-Ps-adjacent (adjacent? board (parse-piece-string "bP") (parse-piece-string "wP")),
    :7.2-bP-adjacent-bK (adjacent? board (parse-piece-string "bP") (parse-piece-string "bK")),
    :7.3-bP-adjacent-bQ (adjacent? board (parse-piece-string "bP") (parse-piece-string "bQ")),
    :7.4-wP-adjacent-wK (adjacent? board (parse-piece-string "wP") (parse-piece-string "wK")),
    :7.5-wP-adjacent-wQ (adjacent? board (parse-piece-string "wP") (parse-piece-string "wQ")),
    :7.6-wP-share-diag-wR (share-diag? board (parse-piece-string "wP") (parse-piece-string "wR"))
    })

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

(defn swap-single-location-contents [board]
  (let [p-pos (rand-int 16)
        q-pos (rand-int 16)
        p (get board p-pos)
        q (get board q-pos)]
    (assoc board p-pos q q-pos p)))

(defn swap-n-location-contents [board num-swaps]
  (loop [b (vec board)
         n num-swaps]
    (if (zero? num-swaps)
      (list* b)
      (recur (swap-single-location-contents b)
             (dec n)))))

