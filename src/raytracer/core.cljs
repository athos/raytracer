(ns raytracer.core
  (:require [clojure.browser.dom :as dom]
            [goog.events :as events]
            thinktopic.aljabr.core
            [clojure.core.matrix :as mat]
            [clojure.core.matrix.operators :as ops]))

(enable-console-print!)

(def width 200)
(def height 100)

(defn point-at [{:keys [origin dir]} t]
  (ops/+ origin (ops/* t dir)))

(defn color [{:keys [dir]}]
  (let [dir (mat/normalise dir)
        t (* 0.5 (+ (mat/select dir 1) 1.0))]
    (ops/+ (ops/* (- 1.0 t) [1.0 1.0 1.0])
           (ops/* t [0.5 0.7 1.0]))))

(defn main []
  (let [canvas (dom/get-element :canvas)
        ctx (.getContext canvas "2d")
        pixels (.createImageData ctx width height)
        data (.-data pixels)
        lower-left [-2.0 -1.0 -1.0]
        horizontal [4.0 0.0 0.0]
        vertical [0.0 2.0 0.0]
        origin [0.0 0.0 0.0]]
    (doseq [j (range (dec height) -1 -1)
            i (range width)
            :let [u (/ i width)
                  v (/ j height)
                  r {:origin origin
                     :dir (ops/+ lower-left
                                 (ops/* u horizontal)
                                 (ops/* v vertical))}
                  pos (* (+ (* (- height j) width) i) 4)
                  col (color r)]]
      (aset data (+ pos 0) (int (* 255.99 (mat/select col 0))))
      (aset data (+ pos 1) (int (* 255.99 (mat/select col 1))))
      (aset data (+ pos 2) (int (* 255.99 (mat/select col 2))))
      (aset data (+ pos 3) 255))
    (.putImageData ctx pixels 0 0)))

(events/listen js/window "load" main)

(defn on-js-reload []
  (main))
