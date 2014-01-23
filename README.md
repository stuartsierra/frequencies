# com.stuartsierra/frequencies

Basic statistical computations on frequency maps.

A *frequency map* (freq-map) is a map from observed values to their
frequency in a data set. The Clojure function
`clojure.core/frequencies` produces a frequency map.

If the range of values is relatively small (for example, a few
thousand integers), then the frequency map can completely describe the
distribution of inputs while using much less memory than the original
data set. This makes frequency maps particularly useful for computing
statistics on large collections of discrete values, such as
millisecond time measurements in a benchmark or log.

Even if the range of values is continuous (for example, floating-point
numbers) or very large, a frequency map can *approximate* the
distribution if the values are grouped into "buckets" of a reasonable
size. In this case, the frequency map resembles a histogram. This
library provides helpers for creating bucketed frequency maps.

This library computes basic statistics about a data set using its
frequency map: mean, median, standard deviation, and percentiles.


## Releases and Dependency Information

No binary releases yet. Run `lein install` in this directory to
install the current -SNAPSHOT version.

[Leiningen] dependency information:

    [com.stuartsierra/frequencies "0.1.0-SNAPSHOT"]

[Maven] dependency information:

    <dependency>
      <groupId>com.stuartsierra</groupId>
      <artifactId>frequencies</artifactId>
      <version>0.1.0-SNAPSHOT</version>
    </dependency>

[Leiningen]: http://leiningen.org/
[Maven]: http://maven.apache.org/
[Gradle] dependency information:

    compile "com.stuartsierra:frequencies:0.1.0-SNAPSHOT"

[Clojars]: http://clojars.org/
[Leiningen]: http://leiningen.org/
[Maven]: http://maven.apache.org/
[Gradle]: http://www.gradle.org/


## Dependencies and Compatibility

This library has no dependencies other than Clojure itself.

I have tested this library with Clojure 1.5.1 only.


## Usage

```clojure
(ns examples
  (:require [com.stuartsierra.frequencies :as freq]))
```

Given some input sequence:

```clojure
(defn example-sequence []
  (repeatedly 10000 #(rand-int 500)))
```

Create a frequency map for it by calling `clojure.core/frequencies`:

```clojure
(def freq-map (frequencies (example-sequence)))
```

Then call the `stats` function to get a bunch of summary statistics
about it:

```clojure
(freq/stats freq-map)
;;=> {:mean 250.4989,
;;    :median 252,
;;    :min 0,
;;    :max 499,
;;    :percentiles
;;    {25 125, 50 252, 75 377, 90 451, 95 474, 99 494, 99.9 499},
;;    :sample-count 10000,
;;    :variance 21034.873998789983,
;;    :stdev 145.0340442750942,
;;    :sum 2504989.0}
```

The map elements `:mean`, `:median`, `:min`, and `:max` are
self-explanatory. `:sum` is the sum of all values in the input
sequence. `:sample-count` is the number of elements in the input
sequence.

`:variance` and `:stdev` are the [variance](http://en.wikipedia.org/wiki/Variance)
and [standard deviation](http://en.wikipedia.org/wiki/Standard_deviation), respectively.

`:percentiles` is a map of [percentiles](http://en.wikipedia.org/wiki/Percentile).
For example, the key 75 is associated with the value in the data set
which is *greater than* 75% of the data set. The 50th percentile is
the median. These percentiles are computed by the
[nearest-rank](http://en.wikipedia.org/wiki/Percentile#Nearest_rank)
method.

All of the individual statistics are available as separate functions
in the `com.stuartsierra.frequencies` namespace. Refer to the source
for details.


### Bucketed Frequency Maps

If the range of possible values is large or continuous, you will need
to group them into buckets to create a frequency map.

Given this input sequence:

```clojure
(defn example-continuous-sequence []
  (repeatedly 10000 #(rand)))
```

Create a bucketed frequency map with a bucket-size of 0.001:

```
(def bucket-freq-map
  (freq/bucket-frequencies 0.001 (example-continuous-sequence)))
```

Keys in the bucketed frequency map will be multiples of the bucket size:

```clojure
(take 5 (keys bucket-freq-map))
;;=> (0.001 0.002 0.003 0.004 0.005)
```

Values in the map will be counts of the number of observed values in
the input sequence which are **less than or equal to** the key, but
greater than the next-smallest key. For example, to get the number of
inputs between 0.100 and 0.101, call:

```clojure
(get bucket-freq-map 0.101)
;;=> 9
```

Statistical computations on bucket frequency maps are **approximate**,
but they are computed the same way:

```clojure
(freq/stats bucket-freq-map)
;;=> {:mean 0.4972261999999997,
;;    :median 0.496,
;;    :min 0.001,
;;    :max 1.0,
;;    :percentiles {25 0.244,             
;;                  50 0.496,             
;;                  75 0.749,             
;;                  90 0.898,             
;;                  95 0.9480000000000001,
;;                  99 0.989,             
;;                  99.9 0.999},
;;    :sample-count 10000,
;;    :variance 0.08391493083356001,
;;    :stdev 0.28968073949360185,
;;    :sum 4972.261999999997}
```


## Change Log

* Version 0.1.0-SNAPSHOT



## Acknowledgments

Special thanks to [Craig Andera](https://github.com/candera) for
advice and code review.



## Copyright and License

The MIT License (MIT)

Copyright © 2013 Stuart Sierra

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.

