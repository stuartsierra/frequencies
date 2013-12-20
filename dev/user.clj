(ns user
  "Tools for interactive development with the REPL. This file should
  not be included in a production build of the application."
  (:require
   [clojure.java.io :as io]
   [clojure.java.javadoc :refer (javadoc)]
   [clojure.pprint :refer (pprint)]
   [clojure.reflect :refer (reflect)]
   [clojure.repl :refer (apropos dir doc find-doc pst source)]
   [clojure.set :as set]
   [clojure.string :as str]
   [clojure.test :as test]
   [clojure.tools.namespace.repl :refer (refresh refresh-all)]
   [com.stuartsierra.frequencies :as fm]
   [incanter.io]
   [incanter.core]
   [incanter.stats]))

(def file "/Users/stuart/Desktop/stat-counter/rtt.log")

(defn read-numbers [rdr]
  (when-let [line (.readLine rdr)]
    (lazy-seq
     (cons (Long/parseLong line)
           (read-numbers rdr)))))

(defn get-seq []
  (with-open [rdr (io/reader file)]
    (doall (read-numbers rdr))))

(defn get-dataset []
  (incanter.io/read-dataset "/Users/stuart/Desktop/rtt.log"))

(defn incanter-data [dataset]
  (incanter.core/sel dataset :cols 0))

;; (def b (stat/assoc-stats
;;         (reduce #(stat/add %1 %2)
;;                 (stat/bucket-counter 10)
;;                 (take 123456 (repeatedly #(rand 100000))))))
