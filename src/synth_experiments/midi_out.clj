(ns synth-experiments.midi_out
  (:require
   [overtone.live :refer :all]
   [leipzig.melody :refer [bpm is all phrase then times tempo where wherever with mapthen]]
   [leipzig.scale :refer [lower]]
   [leipzig.scale :as scale]
   [leipzig.live :as live]
   [leipzig.chord :as chord]
   [leipzig.temperament :as temperament]
   [overtone.studio.midi :as studio_midi]))


;;  |1e&a2e&a3e&a4e&a|1e&a2e&a3e&a4e&a|
;; C|--------------0-|--------------0-|
;; H|--0---0---0-----|--0---0---0-----|
;; S|----0-------0---|----0------00---|
;; B|0---0---0---0---|0---0---0-----0-|

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
           (interleave (repeat nil) (cycle [0 0 0 nil])))
   (all :part :hat)))

(def crash-melody
  (->>
   (phrase (take 16 (repeat 1/2))
           (interleave (repeat nil) (cycle [nil nil nil 0])))
   (all :part :hat)))

(def BPM 120)

(def perc-part
  (->>
   (with kick-melody snare-melody hat-melody crash-melody)
   (tempo (bpm BPM))))

(let [receiver (first (midi-connected-receivers))]
  (assert (= (:description receiver) "Circuit") "Not sending to Circuit")
  (defmethod live/play-note :kick [{midi :pitch dur :duration}]
    (if (number? midi)
      (overtone.midi/midi-note receiver 60 100 dur 9)))
  (defmethod live/play-note :snare [{midi :pitch dur :duration}]
    (if (number? midi)
      (overtone.midi/midi-note receiver 62 100 dur 9)))
  (defmethod live/play-note :hat [{midi :pitch dur :duration}]
    (if (number? midi)
      (overtone.midi/midi-note receiver 64 100 dur 9)))
  (defmethod live/play-note :crash [{midi :pitch dur :duration}]
    (if (number? midi)
      (overtone.midi/midi-note receiver 65 100 dur 9)))
  (comment)
  (live/jam (var perc-part))
  (comment
   (live/stop)))
