(ns raytracer.dielectric
  (:require [clojure.core.matrix :as mat]
            [clojure.core.matrix.operators :as ops]
            [raytracer.material :as material]
            [raytracer.ray :as ray]))

(defn params-for-refract [ray rec refr-index]
  (let [prod (mat/dot (:dir ray) (:normal rec))]
    (if (pos? prod)
      {:normal (ops/- (:normal rec))
       :refr-idx refr-index
       :cosine (/ (* refr-index prod) (mat/magnitude (:dir ray)))}
      {:normal (:normal rec)
       :refr-idx (/ refr-index)
       :cosine (/ (- prod) (mat/magnitude (:dir ray)))})))

(defrecord Dielectric [refr-index]
  material/Material
  (scatter [this ray rec]
    (let [ps (params-for-refract ray rec refr-index)
          refracted (material/refract (:dir ray) (:normal ps) (:refr-idx ps))]
      (if (and refracted
               (>= (rand) (material/schlick (:cosine ps) (:refr-idx ps))))
        {:scattered (ray/->Ray (:p rec) refracted)
         :attenuation [1.0 1.0 1.0]}
        (let [reflected (material/reflect (:dir ray) (:normal rec))]
          {:scattered (ray/->Ray (:p rec) reflected)
           :attenuation [1.0 1.0 1.0]})))))
