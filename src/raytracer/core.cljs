(ns raytracer.core
  (:require [clojure.browser.dom :as dom]
            [goog.events :as events]
            thinktopic.aljabr.core
            [clojure.core.matrix :as mat]))

(enable-console-print!)

(def width 200)
(def height 100)

(defn main []
  (let [canvas (dom/get-element :canvas)
        ctx (.getContext canvas "2d")
        pixels (.createImageData ctx width height)
        data (.-data pixels)]
    (doseq [j (range (dec height) -1 -1)
            i (range width)
            :let [pos (* (+ (* j width) i) 4)
                  col [(/ i width) (/ j height) 0.2]]]
      (aset data (+ pos 0) (int (* 255.99 (mat/select col 0))))
      (aset data (+ pos 1) (int (* 255.99 (mat/select col 1))))
      (aset data (+ pos 2) (int (* 255.99 (mat/select col 2))))
      (aset data (+ pos 3) 255))
    (.putImageData ctx pixels 0 0)))

(events/listen js/window "load" main)

(defn on-js-reload []
  (main))
