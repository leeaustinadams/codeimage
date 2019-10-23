(ns codeimage.core
  (:require
   [clojure.string :refer [blank?]]
   [mikera.image.core :as img]
   [mikera.image.colours :as colours]
   [clj-jgit.porcelain :refer :all]
   [stencil.core :refer [render-file]])
  (:gen-class))

(defn get-relative-path [a b]
  "Returns the path of b relative to a"
  (let [a-path (.toPath (java.io.File. a))
        b-path (.toPath (java.io.File. b))]
    (.toString (.relativize a-path b-path))))

(defn emit-html [img-path values]
  (spit "codeimage.html"
        (render-file "codeimage.mustache" {:img-src img-path :values values})))

(def palette (concat [0x590000, 0x592d2d, 0xf2553d, 0xf2beb6, 0xb2622d, 0x593e2d, 0xa6927c, 0xe59900, 0x4c3300, 0x735c00, 0xd9ca00, 0xf2eeb6, 0x5c7300, 0x50592d, 0x99f279, 0x0a4d00, 0x00f200, 0xbfffd9, 0x2d5944, 0x00b377, 0x00ffee, 0x698c8a, 0x003033, 0x39c3e6, 0x1d5673, 0x004480, 0x79aaf2, 0x000033, 0x3d3df2, 0x262699, 0x6559b3, 0x2a2633, 0xb499cc, 0xad33cc, 0x5c3366, 0xf200a2, 0x800055, 0x330d21, 0xb20047, 0xf279aa, 0x8c6977] (repeatedly colours/rand-colour)))
(def criteria [:author :name])

(defn emit-committers [img-path chunks colors]
  (emit-html img-path (map (fn[l c] {:label l :color (format "#%06x" (second c))}) chunks colors)))

(defn -main
  "Entry point"
  [& args]
  (let [[repo-path file-path output-path] args]
    (with-repo repo-path
      (if repo
        (let [file-path (get-relative-path repo-path file-path)]
          (println file-path)
          (if-let [blame (git-blame repo file-path)]
            (let [height (count blame)
                  width (.intValue (/ (* 1 height) 2))
                  img (img/new-image width height false)
                  pixels (img/get-pixels img)
                  chunks (distinct (map #(get-in % criteria) blame))
                  colors (zipmap chunks palette)]
              (println "Chunks: " (count chunks) " WxH: " width height)
              (dotimes [y height]
                (let [c (unchecked-int (get colors (get-in (nth blame y) criteria)))]
                  (dotimes [x width]
                    (aset pixels (+ x (* y width)) c))))
              (img/set-pixels img pixels)
              (if (not (blank? output-path))
                (img/write img output-path "png" :quality 1.0 :progressive false)
                (img/show img :zoom (min 1.0 (/ 768.0 height) (/ 1024.0 width)) :title file-path))
              (emit-committers output-path chunks colors))
            (println "Could not blame " file-path)))
        (println "Repo not found at: " repo-path)))))
