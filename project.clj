(defproject com.troy-west/frequencies "0.1.1-SNAPSHOT"
  :description "Basic statistical computations on frequency maps (histograms)"
  :url "https://github.com/stuartsierra/frequencies"
  :license {:name "The MIT License"
            :url "http://opensource.org/licenses/MIT"}
  :profiles {:dev {:dependencies [[org.clojure/clojure "1.10.0"]
                                  [reiddraper/simple-check "0.5.6"]]}}

  :deploy-repositories [["releases" {:url "https://clojars.org/repo/" :creds :gpg}]]

  :release-tasks [["vcs" "assert-committed"]
                  ["change" "version" "leiningen.release/bump-version" "release"]
                  ["vcs" "commit"]
                  ["vcs" "tag"]
                  ["deploy"]
                  ["change" "version" "leiningen.release/bump-version"]
                  ["vcs" "commit"]
                  ["vcs" "push"]])