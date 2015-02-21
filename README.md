# Pseudo-chess-puzzle
An experiment at evolving solutions to a calendar puzzle.

## Rules

Starting configuration:

0 | 1 | 2 | 3
--- | --- | --- | ---
 |  | BB | 
 |  |  | 
 |  | WK | 
 | BN |  | 
 
* 2 each (1 black, 1 white) of K, Q, R, B, N, and P, so 12 pieces total.
* K's do not share a row, column, or diagonal with each other
* B's do not share a row, column, or diagonal with each other
* N's are not adjacent to each other (including diagonally)
* Q's are not on the same diagonal
* R's have two blank spaces between each other, are each adjacent to exactly one piece, and that piece matches their color
* P's are adjacent to each other and to the K and Q of their matching color. Also white P shares some diagonal with white R.

## Ideas

Use Lexicase, where we have 0/1 or True/False for each condition.

* Start with mutation only just to keep it simple. An "individual" is the sixteen spaces, each being empty or containing one of the 12 pieces. Mutation is shuffling the contents of some pairs spaces. Replace parent if the child is at least as good.
* Alternatively an individual could be the (x, y) locations of the twelve pieces. We could use something like 2-point crossover and mutation of location. This could put multiple pieces on the same location, so we want some sort of error correction, e.g., moving right and down from the specified location until an empty location is found.
