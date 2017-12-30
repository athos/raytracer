(ns raytracer.lambertian
  (:require [clojure.core.matrix :as mat]
            [clojure.core.matrix.operators :as ops]
            [raytracer.material :as material]
            [raytracer.ray :as ray]))

(defn random-in-unit-sphere []
  (loop []
    (let [p (ops/- (ops/* 2.0 [(rand) (rand) (rand)])
                   [1 1 1])]
      (if (< (mat/dot p p) 1)
        p
        (recur)))))

(defrecord Lambertian [albedo]
  material/Material
  (scatter [this ray rec]
    (let [target (ops/+ (:p rec) (:normal rec) (random-in-unit-sphere))]
      {:scattered (ray/->Ray (:p rec) (ops/- target (:p rec)))
       :attenuation albedo})))
