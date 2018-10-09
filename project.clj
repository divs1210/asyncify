(defproject asyncify "0.1.0-SNAPSHOT"
  :description "Event Loop library for Clojure"
  :url "https://github.com/divs1210/asyncify"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/core.async "0.4.474"]
                 [com.climate/claypoole "1.1.4"]]
  :profiles {:dev {:dependencies [[venantius/pyro "0.1.2"]]
                   :injections [(require '[pyro.printer :as printer])
                                (printer/swap-stacktrace-engine!)]}})
