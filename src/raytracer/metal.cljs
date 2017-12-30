(ns raytracer.metal
  (:require [clojure.core.matrix :as mat]
            [clojure.core.matrix.operators :as ops]
            [raytracer.material :as material]
            [raytracer.ray :as ray]))

(defrecord Metal [albedo fuzz]
  material/Material
  (scatter [this ray rec]
    (let [reflected (material/reflect (mat/normalise (:dir ray))
                                      (:normal rec))
          dir' (ops/+ reflected
                      (ops/* fuzz (material/random-in-unit-sphere)))
          scattered (ray/->Ray (:p rec) dir')]
      (when (> (mat/dot dir' (:normal rec)) 0)
        {:scattered scattered
         :attenuation albedo}))))
