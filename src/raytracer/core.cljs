(ns raytracer.core
  (:require [clojure.browser.dom :as dom]
            [goog.events :as events]))

(enable-console-print!)

(def width 500)
(def height 250)

(defn main []
  (let [canvas (dom/get-element :canvas)
        ctx (.getContext canvas "2d")
        pixels (.createImageData ctx width height)
        data (.-data pixels)]
    (doseq [i (range (dec height) -1 -1)
            j (range width)
            :let [pos (* (+ (* i width) j) 4)
                  r (int (* 255.99 (/ i width)))
                  g (int (* 255.99 (/ j height)))
                  b (int (* 255.99 0.2))]]
      (aset data (+ pos 0) r)
      (aset data (+ pos 1) g)
      (aset data (+ pos 2) b)
      (aset data (+ pos 3) 255))
    (.putImageData ctx pixels 0 0)))

(events/listen js/window "load" main)

(defn on-js-reload [])
