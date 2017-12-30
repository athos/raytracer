(ns raytracer.material
  (:require [clojure.core.matrix :as mat]
            [clojure.core.matrix.operators :as ops]))

(defprotocol Material
  (scatter [this ray record]))

(defn reflect [v n]
  (ops/- v (ops/* (* 2 (mat/dot v n)) n)))
