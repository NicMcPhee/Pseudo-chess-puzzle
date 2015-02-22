(ns pseudo-chess-puzzle.core
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [pseudo_chess_puzzle.location_genome :as lg]))

(defn setup []
  ; Set frame rate to 30 frames per second.
  (q/frame-rate 30)
  ; Set color mode to HSB (HSV) instead of default RGB.
  (q/color-mode :hsb)
  ; setup function returns initial state, i.e., the initial
  ; board setup.
  (lg/initial-genome))

(defn update-state [state]
  ; Update sketch state; this does nothing for now.
  (lg/swap-n-location-contents state (rand-int 3)))

; Unfortunately I can't (immediately) use this because
; the default font used by Quil doesn't support these
; symbols in a variety of sizes.
(def piece-image-map
  {
   {:color :white, :piece :K} \♔
   {:color :white, :piece :Q} \♕
   {:color :white, :piece :R} \♖
   {:color :white, :piece :B} \♗
   {:color :white, :piece :N} \♘
   {:color :white, :piece :P} \♙
   {:color :black, :piece :K} \♚
   {:color :black, :piece :Q} \♛
   {:color :black, :piece :R} \♜
   {:color :black, :piece :B} \♝
   {:color :black, :piece :N} \♞
   {:color :black, :piece :P} \♟
   {}                         \space
   }
  )

(defn draw-state [state]
  ; Clear the sketch by filling it with light-grey color.
  (q/background 240)
  (q/fill 15)
  (q/text-size 36)
  (let [pieces (map lg/position-string-representation state)
        rows (partition 4 pieces)
        block-size 100]
    (doseq [row-num (range 4)
            col-num (range 4)
            :let [piece (nth (nth rows row-num) col-num)
                  x-coord (+ 75 (* col-num block-size))
                  y-coord (- 500 (+ 75 (* (- 3 row-num) block-size)))]]
      (q/text piece x-coord y-coord)))
  (q/text-size 24)
  (let [error-vector (lg/error-vector state)
        bits (apply str (map #(if (get error-vector %) 1 0) (sort (keys error-vector))))]
    (q/text bits 50 50)))

(q/defsketch pseudo-chess-puzzle
  :title "Pseudo-chess puzzle"
  :size [500 500]
  ; setup function called only once, during sketch initialization.
  :setup setup
  ; update-state is called on each iteration before draw-state.
  :update update-state
  :draw draw-state
  ; This sketch uses functional-mode middleware.
  ; Check quil wiki for more info about middlewares and particularly
  ; fun-mode.
  :middleware [m/fun-mode])
