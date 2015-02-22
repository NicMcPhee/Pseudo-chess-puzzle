(defproject semi-chess-puzzle "0.3.0"
  :description "A simple hill-climbing system to solve a small puzzle"
  :url "https://github.com/NicMcPhee/pseudo-chess-puzzle"
  :license {:name "MIT License"
            :url "http://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [quil "2.2.5" :exclusions [org.clojure/clojure]]]
  :profiles {:dev {:dependencies [[midje "1.6.3"]]}
             :uberjar {:aot :all}}
  :main pseudo-chess-puzzle.core
  :target-path "target/%s")
