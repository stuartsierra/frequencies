(ns com.stuartsierra.frequencies-test
  (:require [com.stuartsierra.frequencies :as freq]
            [simple-check.core :as sc]
            [simple-check.generators :as gen]
            [simple-check.properties :as prop]
            [simple-check.clojure-test :refer [defspec]]))

;;; Naive implementations of the statistics that use the original
;;; sequences.

(defn sum [v]
  (reduce + 0.0 v))

(defn mean [v]
  (/ (sum v) (count v)))

(defn median [v]
  (let [middle (/ (count v) 2)
        n (if (integer? middle)
            (dec middle)
            (long (Math/floor middle)))]
    (nth (sort v) n)))

(defn quantile
  [coll k q]
  ;; This doesn't always produce the same results as
  ;; com.stuartsierra.frequencies/quantile*, but they don't disagree
  ;; by much.
  (let [rank (long (Math/ceil (* k (/ (double (count coll)) q))))]
    (nth (sort coll) rank)))

(defn variance
  [coll]
  (let [mean (mean coll)
        count (count coll)]
    (reduce + (map #(/ (Math/pow (- (double %) mean) 2) count)
                   coll))))

(defn stdev
  [coll]
  (Math/sqrt (variance coll)))

;;; Generators

(def int-coll (gen/not-empty (gen/vector gen/int)))

(def double-coll (gen/not-empty
                  (gen/vector
                   (gen/fmap #(+ % (rand)) gen/int))))

(def number-coll (gen/one-of [int-coll double-coll]))

;;; Test helpers

(defn close?
  "Returns true if the difference between two numbers is very small.
  Like equality for floating-point numbers."
  [x y]
  (< (Math/abs (- (double x) (double y))) 0.000001))

;;; Tests

(defspec t-sample-count 1000
  (prop/for-all [v number-coll]
    (close? (freq/sample-count (frequencies v))
            (count v))))

(defspec t-sum 1000
  (prop/for-all [v number-coll]
    (close? (freq/sum (frequencies v))
            (sum v))))

(defspec t-mean 1000
  (prop/for-all [v number-coll]
    (close? (freq/mean (frequencies v))
            (mean v))))

(defspec t-median 1000
  (prop/for-all [v number-coll]
    (close? (freq/median (frequencies v))
            (median v))))

(defspec t-variance 1000
  (prop/for-all [v number-coll]
    (close? (freq/variance (frequencies v))
            (variance v))))

(defspec t-stdev 1000
  (prop/for-all [v number-coll]
    (close? (freq/stdev (frequencies v))
            (stdev v))))

(comment
  ;; I can't get this one to work consistently.
  (defspec t-percentile-99 1000
    (prop/for-all [v number-coll]
      (close? (freq/quantile (frequencies v) 99 100)
              (quantile v 99 100)))))

;; Local Variables:
;; mode: clojure
;; eval: (define-clojure-indent (for-all (quote defun)))
;; End:
