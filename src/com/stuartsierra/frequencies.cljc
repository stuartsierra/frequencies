(ns com.stuartsierra.frequencies
  "Basic statistical computations on frequency maps. A frequency map
  (freq-map) is a map from observed values to their frequency in a
  data set.

  If the observed values are all integers within a small range, then a
  frequency map may be exact, such as that returned by
  clojure.core/frequencies. Floating-point values or a large range of
  integers can be grouped into 'buckets' as in a histogram: the
  'bucket-frequencies' function does this.

  You can create your own bucketed frequency map (for example, as part
  of a larger 'reduce' operation) using the functions 'bucket' and
  'recover-bucket-keys'.")

(defn bucket
  "Returns an integer bucket ID for the observed value. Bucket maps
  use integers as map keys to avoid possible errors from
  floating-point arithmetic."
  [bucket-size value]
  (long (Math/ceil (/ (double value) bucket-size))))

(defn recover-bucket-keys
  "Converts the keys of a map from integer bucket IDs to the original
  value domain. Use this only if you are building up a bucket map
  yourself with the 'bucket' function; the bucket-frequencies function
  calls recover-keys automatically."
  [bucket-map bucket-size]
  (reduce-kv (fn [m k v]
               (assoc m (* k bucket-size) v))
             (sorted-map)
             bucket-map))

(defn bucket-frequencies
  "Returns a bucketed frequency map. Keys in the map are values from
  the input, rounded up to bucket-size. Values in the map are counts
  of the number of occurances of values less than or equal to their
  bucket key but greater than the next-lowest bucket key."
  [bucket-size values]
  (-> (reduce (fn [freq-map value]
                (let [b (bucket bucket-size value)
                      freq (get freq-map b 0)]
                  (assoc! freq-map b (inc freq))))
              (transient {})
              values)
      persistent!
      (recover-bucket-keys bucket-size)))

(defn sum
  "Returns the sum of all observed values in a frequency map."
  [freq-map]
  (reduce-kv (fn [sum value frequency]
               (+ sum (* (double value) frequency)))
             0.0
             freq-map))

(defn sample-count
  "Returns the number of observed values in a frequency map."
  [freq-map]
  (reduce + (vals freq-map)))

(defn mean
  "Returns the mean (average) of observed values in a frequency map."
  [freq-map]
  (let [sample-count (sample-count freq-map)
        sum (sum freq-map)]
    (/ sum sample-count)))

(defn values
  "Returns a lazy sequence of all the observed values, repeating each
  value the number of times it was observed."
  [freq-map]
  (when-let [entry (first freq-map)]
    (let [[value frequency] entry]
      (lazy-seq (concat (repeat frequency value)
                        (values (rest freq-map)))))))

(defn quantile*
  "Like quantile but takes sample-count as an argument. For when you
  already know the sample-count and don't want to recompute it. Also
  assumes that the frequency map is already sorted."
  [sorted-freq-map k q sample-count]
  (let [rank (long (Math/ceil (* k (/ (double sample-count) q))))]
    (loop [m (seq sorted-freq-map)
           lower 0
           prev-value #?(:clj  Double/NEGATIVE_INFINITY
                         :cljs (.-NEGATIVE_INFINITY js/Number))]
      (if-let [entry (first m)]
        (let [[value freq] entry
              upper (+ lower freq)]
          (if (<= rank upper)
            value
            (recur (rest m) upper value)))
        prev-value))))

(defn- ensure-sorted [m]
  (if (sorted? m)
    m
    (into (sorted-map) m)))

(defn quantile
  "Returns the value which is greater than k/q of the observed values
  in the frequency map. For example, k=1 q=2 is the median; k=99 q=100
  is the 99th percentile. For bucketed frequency maps, returns the
  nearest bucket."
  [freq-map k q]
  (quantile* (ensure-sorted freq-map) k q (sample-count freq-map)))

(defn median
  "Returns the median of the observed values in the frequency map."
  [freq-map]
  (quantile freq-map 1 2))

(defn percentiles*
  "Like percentiles but the sample-count is provided as an argument
  instead of computed, and the frequency map must already be sorted."
  [sorted-freq-map percentiles sample-count]
  (reduce (fn [m k]
            (assoc m (keyword (str "p" k)) (quantile* sorted-freq-map k 100.0 sample-count)))
          (sorted-map)
          percentiles))

(defn percentiles
  "Returns a map of percentile values from the frequency map. Argument
  'percentiles' is a collection of percentile targets, which will be
  keys in the returned map. For example, a percentiles argument of 
  [25 50 99.9] would return a map containing the 25th, 50th (median), 
  and 99.9th percentile."
  [freq-map percentiles]
  (percentiles* (ensure-sorted freq-map)
                percentiles
                (sample-count freq-map)))

(defn variance*
  "Like 'variance' but takes the mean and sample count as arguments
  instead of computing them."
  [freq-map mean sample-count]
  (reduce-kv (fn [sum value frequency]
               (let [p (/ (double frequency) sample-count)
                     diff (- (double value) mean)
                     diff-squared (* diff diff)]
                 (+ sum (* p diff-squared))))
             0
             freq-map))

(defn variance
  "Returns the variance of observed values in a frequency map."
  [freq-map]
  (variance* freq-map (mean freq-map) (sample-count freq-map)))

(defn stdev
  "Returns the standard deviation (square root of the variance) of
  observed values in a frequency map."
  [freq-map]
  (Math/sqrt (variance freq-map)))

(defn stats
  "Returns a map of statistics for the frequency map with the
  following keys:

  :mean, :median, :variance, :stdev, :sum, :sample-count,

  :min  minimum observed value;

  :max  maximum observed value;

  :percentiles    Map of percentile level to observed value. 
     Defaults to quartiles and 90, 95, 99, and 99.9th percentiles.
     Change the returned percentiles by passing a vector of percentile
     levels (between 0 and 100) as the option :percentiles."
  [freq-map & {:keys [percentiles]
               :or {percentiles [25 50 75 90 95 99 99.9]}}]
  (let [sorted-freq-map (ensure-sorted freq-map)
        sum (sum sorted-freq-map)
        sample-count (sample-count sorted-freq-map)
        mean (/ (double sum) sample-count)
        variance (variance* sorted-freq-map mean sample-count)
        stdev (Math/sqrt variance)
        min (first (keys sorted-freq-map))
        max (last (keys sorted-freq-map))
        percentiles (percentiles* sorted-freq-map
                                  percentiles
                                  sample-count)
        median (or (get percentiles :p50)
                   (quantile* sorted-freq-map 1 2 sample-count))]
    (array-map
     :mean mean
     :median median
     :min min
     :max max
     :percentiles percentiles
     :sample-count sample-count
     :variance variance
     :stdev stdev
     :sum sum)))
