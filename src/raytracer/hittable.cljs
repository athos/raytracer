(ns raytracer.hittable)

(defn init-record []
  {:t 0, :p [0 0 0], :normal [0 0 0] :material nil})

(defprotocol Hittable
  (hit* [this ray t-min t-max record]))

(defn hit
  ([hittable ray]
   (hit hittable ray 0.0 ##Inf (init-record)))
  ([hittable ray t-min t-max record]
   (hit* hittable ray t-min t-max record)))

(extend-type PersistentVector
  Hittable
  (hit* [hittables ray t-min t-max record]
    (let [closest (volatile! t-max)
          hits-any? (volatile! false)
          rec (reduce (fn [rec hittable]
                        (if-let [rec' (hit* hittable ray
                                            t-min @closest rec)]
                          (do (vreset! closest (:t rec'))
                              (vreset! hits-any? true)
                              rec')
                          rec))
                      record
                      hittables)]
      (when @hits-any?
        rec))))
