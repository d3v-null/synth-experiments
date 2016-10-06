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

;; PERC
;; ===

;; ONLY
;; ----

;;  |1e&a2e&a3e&a4e&a|1e&a2e&a3e&a4e&a|
;; C|--------------0-|--------------0-|
;; H|--0---0---0-----|--0---0---0-----|
;; S|----0-------0---|----0------00---|
;; B|0---0---0---0---|0---0---0-----0-|

(def kick-melody-only
  (->>
   ()
   (then (times 7 (phrase [1/4 3/4]        [0 nil])))
   (then (times 1 (phrase [1/2 1/4 1/4]    [nil 0 nil])))
   (all :part :kick)))

(def snare-melody-only
  (->>
   ()
   (then (times 3 (phrase [1 1/4 3/4]      [nil 0 nil])))
   (then (times 1 (phrase [3/4 1/4]        [nil 0])))
   (then (times 1 (phrase [1/4 3/4]        [0 nil])))
   (all :part :snare)))

(def hat-melody-only
  (->>
   ()
   (then (times 7 (phrase [1/2 1/2]       [nil 0])))
   (then (times 1 (phrase [1]       [nil])))
   (all :part :hat)))

(def crash-melody-only
  (->>
   ()
   (then (times 7 (phrase [1] [nil])))
   (then (times 1 (phrase [1/2 1/2] [nil 0])))
   (all :part :crash)))

(def perc-melody-only
  (->>
   (with kick-melody-only snare-melody-only hat-melody-only crash-melody-only)))


;; GENERIC
;; -------

;;  |1e&a2e&a3e&a4e&a|1e&a2e&a3e&a4e&a|
;; C|----------------|--------------0-|
;; H|--0---0---0---0-|--0---0---0-----|
;; S|----0-------0---|----0-------0---|
;; B|0-------0-------|0-------0-------|
;
; (def kick-melody-generic
;   (->>
;    (phrase (concat (take 14 (cycle [1/4 3/4 1/4 3/4])) [1/4 1/4 1/4 1/4])
;            (concat (take 14 (cycle [  0 nil   1 nil])) [  1 nil   1 nil]))
;    (all :part :kick)))
;
; (def snare-melody-generic
;   (->>
;    (phrase (concat [  1] (take 5 (cycle [1/4 7/4])) [6/4 1/4 1/4 3/4])
;            (concat [nil] (take 5 (cycle [  0 nil])) [nil   0   0 nil]))
;    (all :part :snare)))
;
; (def hat-melody-generic
;   (->>
;    (phrase (take 16 (repeat 1/2))
;            (interleave (repeat nil) (cycle [0 0 0 nil])))
;    (all :part :hat)))
;
; (def crash-melody-generic
;   (->>
;    (phrase (take 16 (repeat 1/2))
;            (interleave (repeat nil) (cycle [nil nil nil 0])))
;    (all :part :crash)))
;
; (def perc-melody-generic
;   (->>
;    (with kick-melody-generic snare-melody-generic hat-melody-generic crash-melody-generic)))

(def BPM 120)

(def perc-part-only
  (->>
   perc-melody-only
   (tempo (bpm BPM))))


;; BASS
;; ====

;;  |1e&a2e&a3e&a4e&a|1e&a2e&a3e&a4e&a|
;;+3|----------------|------0---------|
;;+2|------------0---|----0---0-------|
;;+1|----------------|----------------|
;;+0|0-------0-------|0-----------0---|
;;-1|------0---------|----------------|
;;-2|----------------|----------------|
;;-3|----0-----------|----------------|

(def progression [0 0 3 0 4 0])

(defn bassline [root]
  (->> (phrase (cycle [1 1/2 1/2 1 1]) [0 -3 -1 0 2 0 2 3 2 0])
       (where :pitch (scale/from root))
       (where :pitch (comp scale/lower scale/lower))
       (all :part :bass)))

(def bass-part
  (->>
   (mapthen bassline progression)
   (where :pitch (comp scale/B scale/minor))))
  ;  (tempo (bpm BPM))))

(let [receiver (first (filter #(= (:description %) "Circuit")
                              (studio_midi/midi-connected-receivers)))]
  ; (assert (= (:description receiver) "Circuit") "Not sending to Circuit")
  (defmethod live/play-note :kick [{midi :pitch dur :duration}]
    (if (number? midi)
      (studio_midi/midi-note receiver 60 100 (* 900 dur) 9)))
  (defmethod live/play-note :snare [{midi :pitch dur :duration}]
    (if (number? midi)
      (studio_midi/midi-note receiver 62 100 (* 900 dur) 9)))
  (defmethod live/play-note :hat [{midi :pitch dur :duration}]
    (if (number? midi)
      (studio_midi/midi-note receiver 64 100 (* 900 dur) 9)))
  (defmethod live/play-note :crash [{midi :pitch dur :duration}]
    (if (number? midi)
      (studio_midi/midi-note receiver 65 100 (* 900 dur) 9)))
  (defmethod live/play-note :bass [{midi :pitch dur :duration}]
    (if (number? midi)
      (studio_midi/midi-note receiver midi 100 (* 900 dur) 0))))

(def track
  (->>
   bass-part
   (with (times 6 perc-melody-only))
   (tempo (bpm BPM))))


(comment
  (live/jam (var track))
  (live/jam (var perc-part-only))
  (live/jam (var bass-part)))
(comment
 (live/stop))
