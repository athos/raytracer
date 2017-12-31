(ns raytracer.core
  (:require [clojure.browser.dom :as dom]
            [clojure.core.matrix :as mat]
            [clojure.core.matrix.operators :as ops]
            [goog.events :as events]
            [raytracer.camera :as camera]
            [raytracer.dielectric :as dielectric]
            [raytracer.hittable :as hit]
            [raytracer.lambertian :as lamb]
            [raytracer.material :as material]
            [raytracer.metal :as metal]
            [raytracer.ray :as ray]
            [raytracer.sphere :as sphere]
            thinktopic.aljabr.core))

(enable-console-print!)

(def width 200)
(def height 100)
(def ntimes 10)

(defn color
  ([ray world] (color ray world 0))
  ([{:keys [dir] :as ray} world depth]
   (if-let [rec (hit/hit world ray)]
     (if-let [ret (and (< depth 10)
                       (material/scatter (:material rec) ray rec))]
       (ops/* (:attenuation ret)
              (color (:scattered ret) world (inc depth)))
       [0.0 0.0 0.0])
     (let [dir (mat/normalise dir)
           t (* 0.5 (+ (mat/select dir 1) 1.0))]
       (ops/+ (ops/* (- 1.0 t) [1.0 1.0 1.0])
              (ops/* t [0.5 0.7 1.0]))))))

(defn sample-colors [camera i j world]
  (-> (reduce (fn [col _]
                (let [u (/ (+ i (rand)) width)
                      v (/ (+ j (rand)) height)
                      ray (camera/ray-at camera u v)]
                  (ops/+ col (color ray world))))
              [0 0 0]
              (range ntimes))
      (ops// ntimes)))

(defn write-pixel [data i j col]
  (let [pos (* (+ (* (- height j) width) i) 4)]
    (aset data (+ pos 0) (int (* 255.99 (mat/select col 0))))
    (aset data (+ pos 1) (int (* 255.99 (mat/select col 1))))
    (aset data (+ pos 2) (int (* 255.99 (mat/select col 2))))
    (aset data (+ pos 3) 255)))

(def world
  [(sphere/->Sphere [0 0 -1]
                    0.5
                    (lamb/->Lambertian [0.1 0.2 0.5]))
   (sphere/->Sphere [0 -100.5 -1]
                    100
                    (lamb/->Lambertian [0.8 0.8 0.0]))
   (sphere/->Sphere [1 0 -1]
                    0.5
                    (metal/->Metal [0.8 0.6 0.2] 0.0))
   (sphere/->Sphere [-1 0 -1]
                    0.5
                    (dielectric/->Dielectric 1.5))])

(defn main []
  (let [canvas (dom/get-element :canvas)
        ctx (.getContext canvas "2d")
        pixels (.createImageData ctx width height)
        data (.-data pixels)
        camera (camera/make-camera)]
    (doseq [j (range (dec height) -1 -1)
            i (range width)
            :let [col (sample-colors camera i j world)]]
      (write-pixel data i j (mat/sqrt col)))
    (.putImageData ctx pixels 0 0)))

(events/listen js/window "load" main)

(defn on-js-reload []
  (main))
