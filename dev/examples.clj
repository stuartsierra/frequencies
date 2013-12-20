(ns examples
  (:require [com.stuartsierra.frequencies :as freq]))

(defn example-sequence []
  (repeatedly 10000 #(rand-int 500)))

(def freq-map (frequencies (example-sequence)))

(defn example-continuous-sequence []
  (repeatedly 10000 #(rand)))

(def bucket-freq-map
  (freq/bucket-frequencies 0.001 (example-continuous-sequence)))

(comment

(freq/stats freq-map)

(take 5 (keys bucket-freq-map))
;;=> (0.001 0.002 0.003 0.004 0.005)

(get bucket-freq-map 0.101)

(freq/stats bucket-freq-map)

) ; end comment
