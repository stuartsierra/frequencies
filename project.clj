(defproject com.stuartsierra/frequencies "0.1.1-SNAPSHOT"
  :description "Basic statistical computations on frequency maps (histograms)"
  :url "https://github.com/stuartsierra/frequencies"
  :license {:name "The MIT License"
            :url "http://opensource.org/licenses/MIT"}
  :profiles {:dev {:dependencies [[org.clojure/clojure "1.5.1"]
                                  [reiddraper/simple-check "0.5.3"]]}
             :clj1.4 {:dependencies [[org.clojure/clojure "1.4.0"]
                                     [reiddraper/simple-check "0.5.3"]]}})
