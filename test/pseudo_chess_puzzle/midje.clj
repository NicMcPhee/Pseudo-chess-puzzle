(ns pseudo_chess_puzzle.midje
  (:use [midje.sweet])
  (:require [pseudo_chess_puzzle.location_genome :as lg]))

(facts "about initial position-based genome"
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


(fact "`position-string-representation` generates an algebraic representation of a piece"
      (lg/position-string-representation { :color :black :piece :B }) => "bB"
      (lg/position-string-representation { :color :white :piece :B }) => "wB"
      (lg/position-string-representation { :color :black :piece :K }) => "bK"
      (lg/position-string-representation { :color :white :piece :R }) => "wR")

(fact "`share-row?` returns `true` exactly when two pieces share a row"
      ; I'm going to cheat and do the tests with integer values instead of actual
      ; pieces, as that cuts down substantially on the clutter here.
      (let [test-board (range 16)]
        (lg/share-row? test-board 1 9)  => false
        (lg/share-row? test-board 7 1)  => false
        (lg/share-row? test-board 6 8)  => false
        (lg/share-row? test-board 11 6) => false
        (lg/share-row? test-board 7 15) => false
        (lg/share-row? test-board 14 1) => false
        (lg/share-row? test-board 4 10) => false
        (lg/share-row? test-board 11 1) => false
        (lg/share-row? test-board 14 6) => false

        (lg/share-row? test-board 2 0)   => true
        (lg/share-row? test-board 7 4)   => true
        (lg/share-row? test-board 8 9)   => true
        (lg/share-row? test-board 13 15) => true
        ))

(fact "`share-col?` returns `true` exactly when two pieces share a column"
      (let [test-board (range 16)]
        (lg/share-col? test-board  3  9) => false
        (lg/share-col? test-board  7  9) => false
        (lg/share-col? test-board 11  1) => false
        (lg/share-col? test-board 12 13) => false
        (lg/share-col? test-board 14 15) => false
        (lg/share-col? test-board 13 14) => false
        (lg/share-col? test-board  6  5) => false
        (lg/share-col? test-board 10  3) => false

        (lg/share-col? test-board  0 12) => true
        (lg/share-col? test-board  9  1) => true
        (lg/share-col? test-board  0 12) => true
        (lg/share-col? test-board 14 10) => true
        ))

(fact "`share-diag?` returns `true` exactly when two pieces share a (not necessarily main) diagonal"
      (let [test-board (range 16)]
        (lg/share-diag? test-board 13 15) => false
        (lg/share-diag? test-board  1 10) => false
        (lg/share-diag? test-board 11 12) => false
        (lg/share-diag? test-board 10  3) => false
        (lg/share-diag? test-board  2 11) => false
        (lg/share-diag? test-board  7 14) => false
        (lg/share-diag? test-board  0  1) => false
        (lg/share-diag? test-board 13  9) => false
        (lg/share-diag? test-board  1  8) => false
        (lg/share-diag? test-board  2 12) => false

        (lg/share-diag? test-board 10  0) => true
        (lg/share-diag? test-board 13  8) => true
        (lg/share-diag? test-board  4 14) => true
        (lg/share-diag? test-board  1  6) => true
        (lg/share-diag? test-board  2  7) => true
        (lg/share-diag? test-board  4  1) => true
        (lg/share-diag? test-board  5  8) => true
        (lg/share-diag? test-board 12  6) => true
        (lg/share-diag? test-board  7 13) => true
        (lg/share-diag? test-board 11 14) => true
        ))

(fact "`adjacent?` returns `true` exactly when two pieces are immediately adjacent (including along a diagonal)"
      (let [test-board (range 16)]
        (lg/adjacent? test-board  2 11) => false
        (lg/adjacent? test-board 10  0) => false
        (lg/adjacent? test-board 14  8) => false
        (lg/adjacent? test-board  2 11) => false
        (lg/adjacent? test-board  1 11) => false
        (lg/adjacent? test-board  9 11) => false
        (lg/adjacent? test-board  5 14) => false
        (lg/adjacent? test-board 12 10) => false
        (lg/adjacent? test-board  4  3) => false

        (lg/adjacent? test-board  9 10) => true
        (lg/adjacent? test-board  9  6) => true
        (lg/adjacent? test-board  4  5) => true
        (lg/adjacent? test-board 15 10) => true
        (lg/adjacent? test-board 14 11) => true
        (lg/adjacent? test-board  5  0) => true
        (lg/adjacent? test-board  9 12) => true
        ))

(fact "`two-apart?` returns `true` exactly when the two pieces are exactly two spaces apart, horizontally or vertically"
      (let [test-board (range 16)]
        (lg/two-apart? test-board  3  9) => false
        (lg/two-apart? test-board  9  5) => false
        (lg/two-apart? test-board  0 13) => false
        (lg/two-apart? test-board 15  4) => false
        (lg/two-apart? test-board 13  7) => false
        (lg/two-apart? test-board 13 14) => false
        (lg/two-apart? test-board  3  6) => false
        (lg/two-apart? test-board  2 15) => false
        (lg/two-apart? test-board  7  6) => false
        (lg/two-apart? test-board  8  2) => false

        (lg/two-apart? test-board  8 11) => true
        (lg/two-apart? test-board 12 15) => true
        (lg/two-apart? test-board 12  0) => true
        (lg/two-apart? test-board 14  2) => true
        (lg/two-apart? test-board  4  7) => true
        (lg/two-apart? test-board  3 15) => true
        (lg/two-apart? test-board 15 12) => true
        ))

(defn make-board [board-string]
  (let [piece-strings (clojure.string/split board-string #" ")]
    (map lg/parse-piece-string piece-strings)))

(fact "`initial-pieces-correct?` returns true exactly when the 3 initially placed pieces are correct"
      (let [test-board (make-board "bK sp bB wP sp sp wN wQ bP bR wK wR wB bN bQ sp")]
        (lg/initial-pieces-correct? test-board) => true))

(fact "`error-vector` correctly computes the error vector"
      (let [test-board (make-board "bK sp bB wP sp sp wN wQ bP bR wK wR wB bN bQ sp")]
        (lg/error-vector test-board) => { :1-orig-placement true,
                                          :2-Ks-not-share false,
                                          :3-Bs-not-share true,
                                          :4-Ns-not-adjacent true,
                                          :5-Qs-share-diag false,
                                          :6.1-Rs-two-spaces false,
                                          :6.2-bR-adjacent-one false,
                                          :6.3-wR-adjacent-one false,
                                          :6.4-bR-adjacent-black false,
                                          :6.5-wR-adjacent-white false,
                                          :7.1-Ps-adjacent false,
                                          :7.2-bP-adjacent-bK false,
                                          :7.3-bP-adjacent-bQ false,
                                          :7.4-wP-adjacent-wK false,
                                          :7.5-wP-adjacent-wQ true,
                                          :7.6-wP-share-diag-wR false})
      (let [test-board (make-board "wP bP bB sp sp bK sp wB wR bQ wK sp bR bN wQ wN")]
        (lg/error-vector test-board) => { :1-orig-placement true,
                                          :2-Ks-not-share false,
                                          :3-Bs-not-share false,
                                          :4-Ns-not-adjacent true,
                                          :5-Qs-share-diag true,
                                          :6.1-Rs-two-spaces false,
                                          :6.2-bR-adjacent-one false,
                                          :6.3-wR-adjacent-one false,
                                          :6.4-bR-adjacent-black false,
                                          :6.5-wR-adjacent-white false,
                                          :7.1-Ps-adjacent true,
                                          :7.2-bP-adjacent-bK true,
                                          :7.3-bP-adjacent-bQ false,
                                          :7.4-wP-adjacent-wK false,
                                          :7.5-wP-adjacent-wQ false,
                                          :7.6-wP-share-diag-wR false})
      (let [test-board (make-board "bR sp sp wR bK sp sp wN bQ wB wK wP wQ bN bP bB")]
        (lg/error-vector test-board) => { :1-orig-placement false,
                                          :2-Ks-not-share true,
                                          :3-Bs-not-share true,
                                          :4-Ns-not-adjacent true,
                                          :5-Qs-share-diag false,
                                          :6.1-Rs-two-spaces true,
                                          :6.2-bR-adjacent-one true,
                                          :6.3-wR-adjacent-one true,
                                          :6.4-bR-adjacent-black true,
                                          :6.5-wR-adjacent-white true,
                                          :7.1-Ps-adjacent true,
                                          :7.2-bP-adjacent-bK false,
                                          :7.3-bP-adjacent-bQ false,
                                          :7.4-wP-adjacent-wK true,
                                          :7.5-wP-adjacent-wQ false,
                                          :7.6-wP-share-diag-wR false})
      )
