(ns raytracer.ray
  (:require [clojure.core.matrix.operators :as ops]))

(defrecord Ray [origin dir])

(defn point-at [{:keys [origin dir]} t]
  (ops/+ origin (ops/* t dir)))
