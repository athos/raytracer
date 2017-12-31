(ns raytracer.camera
  (:require [clojure.core.matrix.operators :as ops]
            [raytracer.ray :as ray]
            [clojure.core.matrix :as mat]))

(defrecord Camera [origin horizontal vertical lower-left u v w lens-radius])

(defn random-in-unit-disk []
  (loop []
    (let [p (ops/- (ops/* 2.0 [(rand) (rand) 0]) [1 1 0])]
      (if (>= (mat/dot p p) 1.0)
        (recur)
        p))))

(defn make-camera [look-from look-at vup vfov aspect aperture focus-dist]
  (let [lens-radius (/ aperture 2)
        theta (/ (* vfov js/Math.PI) 180)
        half-height (js/Math.tan (/ theta 2))
        half-width (* aspect half-height)
        origin look-from
        w (mat/normalise (ops/- look-from look-at))
        u (mat/normalise (mat/cross vup w))
        v (mat/cross w u)
        lower-left (ops/- origin
                          (ops/* half-width focus-dist u)
                          (ops/* half-height focus-dist v)
                          (ops/* focus-dist w))
        horizontal (ops/* 2 half-width focus-dist u)
        vertical (ops/* 2 half-height focus-dist v)]
    (->Camera origin horizontal vertical lower-left u v w lens-radius)))

(defn ray-at [camera s t]
  (let [[x y _] (ops/* (:lens-radius camera) (random-in-unit-disk))
        offset (ops/+ (ops/* (:u camera) x) (ops/* (:v camera) y))]
    (ray/->Ray (ops/+ (:origin camera) offset)
               (ops/+ (:lower-left camera)
                      (ops/* s (:horizontal camera))
                      (ops/* t (:vertical camera))
                      (ops/* -1 (:origin camera))
                      (ops/* -1 offset)))))
