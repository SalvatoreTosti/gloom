(defproject gloom "0.1.0-SNAPSHOT"
  :description "gloom game"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [clojure-lanterna "0.9.7"]
                 [quil "3.0.0"]
                 ]
  :aot  [main.main]
  :main main.main)
