(ns pseudo-chess-puzzle.core
  (:require [quil.core :as q :include-macros true]
            [quil.middleware :as m]
            [pseudo_chess_puzzle.location_genome :as lg]))

(defn setup []
  ; Set frame rate to 30 frames per second.
  (q/frame-rate 30)
  ; Set color mode to HSB (HSV) instead of default RGB.
  (q/color-mode :hsb)
  ; setup function returns initial state. It contains
  ; circle color and position.
  @lg/best-board)

(defn update-state [state]
  ; Update sketch state by getting the current best state from
  ; the shared atom.
  @lg/best-board)

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
    (q/text bits 50 50))
  (q/text (rand-int 10) 350 50))
  ; (q/text (str @lg/num-iterations) 350 50))

(q/defsketch pseudo-chess-puzzle
  :host "pseudo-chess-puzzle"
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

(defn -main [& args]
  (lg/search))
