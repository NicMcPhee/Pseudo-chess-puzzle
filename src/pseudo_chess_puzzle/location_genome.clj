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

(defn piece-in-position [board index piece-string]
  (= (nth board index) (parse-piece-string piece-string)))

(defn not-share-rcd? [board s t]
  (not (share-row-col-diag? board
                            (parse-piece-string s)
                            (parse-piece-string t))))

(defn one-adjacent? [board piece-string]
  (= 1 (count (adjacent-pieces board (parse-piece-string piece-string)))))

(defn all-adjacent-have-color? [board piece-string color]
  (every? #(= color (:color %)) (adjacent-pieces board (parse-piece-string piece-string))))

(defn adjacent-pieces? [board s t]
  (adjacent? board (parse-piece-string s) (parse-piece-string t)))

(defn error-vector [board]
  { :1.1-bB-orig-placement (piece-in-position board 2 "bB"),
    :1.2-wK-orig-placement (piece-in-position board 10 "wK"),
    :1.3-bN-orig-placement (piece-in-position board 13 "bN"),
    :2-Ks-not-share (not-share-rcd? board "bK" "wK"),
    :3-Bs-not-share (not-share-rcd? board "bB" "wB"),
    :4-Ns-not-adjacent (not (adjacent? board (parse-piece-string "bN") (parse-piece-string "wN"))),
    :5-Qs-share-diag (share-diag? board (parse-piece-string "bQ") (parse-piece-string "wQ")),
    :6.1-Rs-two-spaces (two-apart? board (parse-piece-string "bR") (parse-piece-string "wR")),
    :6.2-bR-adjacent-one (one-adjacent? board "bR"),
    :6.3-wR-adjacent-one (one-adjacent? board "wR"),
    :6.4-bR-adjacent-black (all-adjacent-have-color? board "bR" :black),
    :6.5-wR-adjacent-white (all-adjacent-have-color? board "wR" :white),
    :7.1-Ps-adjacent (adjacent-pieces? board "bP" "wP"),
    :7.2-bP-adjacent-bK (adjacent-pieces? board "bP" "bK"),
    :7.3-bP-adjacent-bQ (adjacent-pieces? board "bP" "bQ"),
    :7.4-wP-adjacent-wK (adjacent-pieces? board "wP" "wK"),
    :7.5-wP-adjacent-wQ (adjacent-pieces? board "wP" "wQ"),
    :7.6-wP-share-diag-wR (share-diag? board (parse-piece-string "wP") (parse-piece-string "wR"))
    })

(defn count-hits [error-vector]
  (count (filter identity (vals error-vector))))

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
  (loop [board (vec board)
         num-swaps num-swaps]
    (if (zero? num-swaps)
      (list* board)
      (recur (swap-single-location-contents board)
             (dec num-swaps)))))

(def best-board (atom (initial-genome)))
(def num-iterations (atom 0))

(defn hill-climb [board]
  (swap! num-iterations inc)
  (let [new-board (swap-n-location-contents board (inc (rand-int 3)))
        old-error-vector (error-vector board)
        old-hits (count-hits old-error-vector)
        new-error-vector (error-vector new-board)
        new-hits (count-hits new-error-vector)]
    (if (>= new-hits old-hits)
      (reset! best-board new-board)
      board)))

(defn search []
  (loop [best-board @best-board]
    (let [errors (error-vector best-board)
          hits (count-hits errors)]
      (if (= hits (count errors))
        best-board
        (recur (hill-climb best-board))))))
