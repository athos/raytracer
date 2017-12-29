(ns raytracer.sphere
  (:require [clojure.core.matrix :as mat]
            [clojure.core.matrix.operators :as ops]
            [raytracer.hittable :as hit]
            [raytracer.ray :as ray]))

(defrecord Sphere [center radius]
  hit/Hittable
  (hit* [this {:keys [origin dir] :as ray} t-min t-max record]
    (let [oc (ops/- origin center)
          a (mat/dot dir dir)
          b (* 2.0 (mat/dot oc dir))
          c (- (mat/dot oc oc) (* radius radius))
          discriminant (- (* b b) (* 4 a c))]
      (when (> discriminant 0)
        (letfn [(update-record [t]
                  (when (< t-min t t-max)
                    (let [p (ray/point-at ray t)
                          normal (ops// (ops/- p center) radius)]
                     (assoc record :t t :p p :normal normal))))]
          (or (update-record (/ (- (- b) (Math/sqrt discriminant))
                                (* 2.0 a)))
              (update-record (/ (+ (- b) (Math/sqrt discriminant))
                                (* 2.0 a)))))))))
