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

(defn summarize-times [seq-of-data-maps]
  (reduce (fn [summary data]
            (-> summary
                ;; maybe compute some other stuff in summary
                (update-in [:times (:time-in-msecs data)]
                           (fnil inc 0))))
          {}
          seq-of-data-maps))

(def bucket-size 1.0)

(defn summarize-values [seq-of-data-maps]
  (-> (reduce (fn [m data]
                (let [value (:floating-point-value data)
                      ;; Get the bucket for this value:
                      bucket (freq/bucket value bucket-size)]
                  ;; Add it to the bucketed frequency map:
                  (update-in m [:buckets bucket] (fnil inc 0))))
              {}
              seq-of-data-maps)
      ;; Finally, replace the keys of the frequency map with their
      ;; actual values
      (update-in [:buckets] freq/recover-bucket-keys bucket-size)))
