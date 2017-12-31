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

(defn refract [v n refr-index]
  (let [uv (mat/normalise v)
        dt (mat/dot uv n)
        discriminant (- 1.0 (* refr-index refr-index (- 1 (* dt dt))))]
    (when (pos? discriminant)
      (ops/- (ops/* refr-index (ops/- uv (ops/* n dt)))
             (ops/* n (js/Math.sqrt discriminant))))))

(defn schlick [cosine refr-index]
  (let [r0 (/ (- 1 refr-index) (+ 1 refr-index))
        r0 (* r0 r0)]
    (+ r0 (* (- 1 r0) (js/Math.pow (- 1 cosine) 5)))))
