(ns raytracer.material
  (:require [clojure.core.matrix :as mat]
            [clojure.core.matrix.operators :as ops]))

(defprotocol Material
  (scatter [this ray record]))

(defn random-in-unit-sphere []
  (loop []
    (let [p (ops/- (ops/* 2.0 [(rand) (rand) (rand)])
                   [1 1 1])]
      (if (< (mat/dot p p) 1)
        p
        (recur)))))

(defn reflect [v n]
  (ops/- v (ops/* (* 2 (mat/dot v n)) n)))
