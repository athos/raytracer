(ns raytracer.camera
  (:require [clojure.core.matrix.operators :as ops]
            [raytracer.ray :as ray]))

(defrecord Camera [origin horizontal vertical lower-left])

(defn make-camera
  ([] (make-camera [0 0 0] [4 0 0] [0 2 0] [-2 -1 -1]))
  ([origin horizontal vertical lower-left]
   (->Camera origin horizontal vertical lower-left)))

(defn ray-at [camera u v]
  (ray/->Ray (:origin camera)
             (ops/+ (:lower-left camera)
                    (ops/* u (:horizontal camera))
                    (ops/* v (:vertical camera)))))
