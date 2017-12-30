(ns raytracer.core
  (:require [clojure.browser.dom :as dom]
            [clojure.core.matrix :as mat]
            [clojure.core.matrix.operators :as ops]
            [goog.events :as events]
            [raytracer.camera :as camera]
            [raytracer.hittable :as hit]
            [raytracer.ray :as ray]
            [raytracer.sphere :as sphere]
            thinktopic.aljabr.core))

(enable-console-print!)

(def width 200)
(def height 100)
(def ntimes 10)
(defn random-in-unit-sphere []
  (loop []
    (let [p (ops/- (ops/* 2.0 [(rand) (rand) (rand)])
                   [1 1 1])]
      (if (< (mat/dot p p) 1)
        p
        (recur)))))

(defn color [{:keys [dir] :as ray} world]
  (if-let [rec (hit/hit world ray)]
    (let [target (ops/+ (:p rec) (:normal rec) (random-in-unit-sphere))]
      (ops/* 0.5 (color (ray/->Ray (:p rec) (ops/- target (:p rec)))
                        world)))
    (let [dir (mat/normalise dir)
          t (* 0.5 (+ (mat/select dir 1) 1.0))]
      (ops/+ (ops/* (- 1.0 t) [1.0 1.0 1.0])
             (ops/* t [0.5 0.7 1.0])))))

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

(defn main []
  (let [canvas (dom/get-element :canvas)
        ctx (.getContext canvas "2d")
        pixels (.createImageData ctx width height)
        data (.-data pixels)
        world [(sphere/->Sphere [0 0 -1] 0.5)
               (sphere/->Sphere [0 -100.5 -1] 100)]
        camera (camera/make-camera)]
    (doseq [j (range (dec height) -1 -1)
            i (range width)
            :let [col (sample-colors camera i j world)]]
      (write-pixel data i j (mat/sqrt col)))
    (.putImageData ctx pixels 0 0)))

(events/listen js/window "load" main)

(defn on-js-reload []
  (main))
