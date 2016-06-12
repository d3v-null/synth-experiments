(ns synth-experiments.sequencing
  (:use [overtone.live])
  (:require
   [leipzig.melody :refer [bpm is all phrase then times tempo where wherever with mapthen]]
   [leipzig.scale :refer [lower]]
   [leipzig.scale :as scale]
   [leipzig.live :as live]
   [leipzig.chord :as chord]
   [leipzig.temperament :as temperament]
   [overtone.studio.scope :as scope]
   [overtone.inst.synth :refer :all]
   [overtone.inst.drum :refer :all]
   [clj-time.format :as f]
   [clj-time.core :as t]
   [clj-time.local :as l]))


;;; PERCUSSION

;; (closed-hat)
;; (soft-hat)
;; (hat3)
;; (open-hat)
(def my-closed-hat hat3)
(def my-open-hat open-hat)


;; (kick)
;; (kick2)
;; (kick3)
;; (dub-kick)
(def my-hard-kick kick)
(def my-soft-kick kick2)

;; (snare)
;; (noise-snare)
(def my-snare noise-snare)


(def clap-melody
  (->>
   (phrase [1/4 1/2 1/4 1/2 1/4 1/4]
           [1   nil 1   nil  1  nil])
   (all :part :clap)))

(def kick-melody
  (->>
   (phrase (take 4 (repeat 1/4)) (take 4 (repeat 0)))
   (all :part :kick)))

(defmethod live/play-note :clap [{midi :pitch dur :duration}]
  (if (number? midi)
      (clap)))
(defmethod live/play-note :kick [{midi :pitch dur :duration}]
  (if (number? midi)
    (if (zero? midi)
      (my-hard-kick)
      (my-soft-kick))))

(def perc-part
  (->>
   (with clap-melody kick-melody)
   (tempo (bpm 120))))

(comment
 (live/jam (var perc-part))
 (live/stop))




;;  |1e&a2e&a3e&a4e&a|1e&a2e&a3e&a4e&a|
;; H|--0---0---0---1-|--0---0---0---1-|
;; S|----0-------0---|----0------00---|
;; B|0---1---0---1---|0---1---0-----1-|

(def kick-melody
  (->>
   (phrase (concat (take 14 (cycle [1/4 3/4 1/4 3/4])) [1/4 1/4 1/4 1/4])
           (concat (take 14 (cycle [  0 nil   1 nil])) [  1 nil   1 nil]))
   (all :part :kick)))

(def snare-melody
  (->>
   (phrase (concat [  1] (take 5 (cycle [1/4 7/4])) [6/4 1/4 1/4 3/4])
           (concat [nil] (take 5 (cycle [  0 nil])) [nil   0   0 nil]))
   (all :part :snare)))

(def hat-melody
  (->>
   (phrase (take 16 (repeat 1/2))
           (interleave (repeat nil) (cycle [0 0 0 1])))
   (all :part :hat)))

(defmethod live/play-note :kick [{midi :pitch dur :duration}]
  (if (number? midi)
    (if (zero? midi)
      (my-hard-kick)
      (my-soft-kick))))
(defmethod live/play-note :snare [{midi :pitch dur :duration}]
  (if (number? midi) (my-snare)))
(defmethod live/play-note :hat [{midi :pitch dur :duration}]
  (if (number? midi)
    (if (zero? midi)
      (my-closed-hat)
      (my-open-hat))))

(def BPM 120)

(def perc-part
  (->>
   (with kick-melody snare-melody hat-melody)
   (tempo (bpm BPM))))

(comment
  (live/jam (var perc-part))
  (live/stop))

(defn get-time-str []
  (f/unparse (f/formatter "yyyyMMddhhmmss") (l/local-now)))

(comment
  (recording-start
    (clojure.string/join ["~/Music/overtone/test" (get-time-str) ".wav"]))
  (recording-stop))
