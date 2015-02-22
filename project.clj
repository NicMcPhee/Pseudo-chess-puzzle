(defproject semi-chess-puzzle "0.3.0"
  :description "A simple hill-climbing system to solve a small puzzle"
  :url "https://github.com/NicMcPhee/pseudo-chess-puzzle"
  :license {:name "MIT License"
            :url "http://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [quil "2.2.5" :exclusions [org.clojure/clojure]]
                 [org.clojure/clojurescript "0.0-2740"]]
  :profiles {:dev {:dependencies [[midje "1.6.3"]]}
             :uberjar {:aot :all}}
  :main pseudo-chess-puzzle.core
  :target-path "target/%s"

  :plugins [[lein-cljsbuild "1.0.4"]]
  :hooks [leiningen.cljsbuild]

  :cljsbuild
  {:builds [{:source-paths ["src"]
             :compiler
             {:output-to "js/main.js"
              :output-dir "out"
              :main "pseudo_chess_puzzle.core"
              :optimizations :none
              :pretty-print true}}]})
