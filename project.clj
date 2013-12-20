(defproject com.stuartsierra/frequencies "0.1.0-SNAPSHOT"
  :description "Basic statistical computations on frequency maps (histograms)"
  :url "https://github.com/stuartsierra/frequencies"
  :license {:name "The MIT License"
            :url "http://opensource.org/licenses/MIT"}
  :profiles {:dev {:dependencies [[org.clojure/clojure "1.5.1"]]}
             ;; For checking statistical results with Incanter
             :incanter {:dependencies [[org.clojure/clojure "1.5.1"]
                                       [org.clojure/tools.namespace "0.2.4"]
                                       [org.clojure/tools.nrepl "0.2.3"]
                                       [incanter "1.5.4"]]
                        :source-paths ["dev"]}})
