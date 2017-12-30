(ns raytracer.lambertian
  (:require [clojure.core.matrix :as mat]
            [clojure.core.matrix.operators :as ops]
            [raytracer.material :as material]
            [raytracer.ray :as ray]))

(defrecord Lambertian [albedo]
  material/Material
  (scatter [this ray rec]
    (let [target (ops/+ (:p rec) (:normal rec)
                        (material/random-in-unit-sphere))]
      {:scattered (ray/->Ray (:p rec) (ops/- target (:p rec)))
       :attenuation albedo})))
