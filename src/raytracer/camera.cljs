(ns raytracer.camera
  (:require [clojure.core.matrix.operators :as ops]
            [raytracer.ray :as ray]
            [clojure.core.matrix :as mat]))

(defrecord Camera [origin horizontal vertical lower-left])

(defn make-camera [look-from look-at vup vfov aspect]
  (let [theta (/ (* vfov js/Math.PI) 180)
        half-height (js/Math.tan (/ theta 2))
        half-width (* aspect half-height)
        origin look-from
        w (mat/normalise (ops/- look-from look-at))
        u (mat/normalise (mat/cross vup w))
        v (mat/cross w u)
        lower-left (ops/- origin
                          (ops/* half-width u)
                          (ops/* half-height v)
                          w)
        horizontal (ops/* 2 half-width u)
        vertical (ops/* 2 half-height v)]
    (->Camera origin horizontal vertical lower-left)))

(defn ray-at [camera s t]
  (ray/->Ray (:origin camera)
             (ops/+ (:lower-left camera)
                    (ops/* s (:horizontal camera))
                    (ops/* t (:vertical camera))
                    (ops/* -1 (:origin camera)))))
