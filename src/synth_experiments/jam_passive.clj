(ns synth-experiments.jam-passive
  (:require
   [overtone.live :refer :all
              :rename {midi-inst-controller bad-midi-inst-controller}]
   [leipzig.melody :refer [bpm is all phrase then times tempo where wherever with mapthen]]
   [leipzig.scale :refer [lower]]
   [leipzig.scale :as scale]
   [leipzig.live :as live]
   [leipzig.chord :as chord]
   [leipzig.temperament :as temperament]
   [overtone.studio.scope :as scope]
   [overtone.inst.synth :refer :all]
   [overtone.inst.drum :refer :all]
   [synth-experiments.midi :refer :all]
   [synth-experiments.recording :refer :all]))

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

(defmethod live/play-note :clap [{midi :pitch dur :duration}]
  (if (number? midi)
      (clap)))
(defmethod live/play-note :kick [{midi :pitch dur :duration}]
  (if (number? midi)
    (if (zero? midi)
      (my-hard-kick)
      (my-soft-kick))))
(defmethod live/play-note :metronome [{midi :pitch dur :duration}]
  (kick))

(def clap-melody
  (->>
   (phrase (cycle [1/4 1/2 1/4 1/2 1/4 1/4])
           (cycle [1   nil 1   nil  1  nil]))
   (all :part :clap)))

(def metronome-melody
  (->>
   (phrase (take 4 (repeat 1))
           (take 4 (repeat 1)))
   (all :part :metronome)))


(comment
  (live/play (->> clap-melody
                  (with metronome)
                  (tempo (bpm 120))))
  (live/stop))



(def kick-melody
  (->>
   (phrase (concat (repeat 4 1/4) [3]) (concat (repeat 16 0) [nil]))
   (all :part :kick)))

(def perc-part
  (->>
   (with clap-melody kick-melody)
   (tempo (bpm 120))))

(comment
 (live/jam (var perc-part))
 (live/stop))

;; BASS

(definst passive-bass-patch
  [ note {:default 60 :min 0 :max 127 :step 1}
    exp {:default 0.8 :min 0.0 :max 1.0}
    ;; 0 -> detune down by 1/2 semi, 1 -> detune up...
    detune {:default 0.6 :min 0.0 :max 1.0}
    ; dephase {:default 0.5 :min 0.0 :max 1.0}
    amp-att 0.2 amp-dec 0.2 amp-sus 0.6 amp-rel 0.2
    lpf-att 0.5 lpf-dec 0.2 lpf-sus 0.4 lpf-rel 0.2
    cutoff 0.4
    mix 0.5
    q 0.5
    gate 1]
  (let [freq1 (midicps note)
        freq2 (midicps (+ note detune -0.5))
        amp-env (env-gen (adsr amp-att amp-dec amp-sus amp-dec amp-rel :level exp)
                         :gate gate :action FREE)
        ; phase2 (- dephase 0.5)
        phase2 0
        sig1 (* mix (saw freq1))
        sig2 (* (- 1 mix) (var-saw freq2 phase2))
        lpf-env (env-gen (adsr lpf-att lpf-dec lpf-sus lpf-rel cutoff)
                         :gate gate)
        lpf-sig (* 1000 lpf-env)]
    (-> (- sig1 sig2)
        (rlpf lpf-sig q)
        (* amp-env))))

(def bass-mapping
  {
    73 [:detune   divide127]
    11 [:q        divide127]
    74 [:cutoff   divide127]
    71 [:mix      divide127]
    12 [:lpf-att  inv-divide127]
    13 [:lpf-dec  inv-divide127]
    14 [:lpf-sus  inv-divide127]
    15 [:lpf-rel  inv-divide127]
    16 [:amp-att  inv-divide127]
    17 [:amp-dec  inv-divide127]
    18 [:amp-sus  inv-divide127]
    19 [:amp-rel  inv-divide127]
    20 [:exp      inv-divide127]})

(def bass-state
  (atom {:cutoff  (/ 116 127)
         :exp     (/ 120 127)
         :lpf-att (/   5 127)
         :lpf-rel (/  35 127)
         :lpf-sus (/  30 127)
         :lpf-dec (/  64 127)
         :amp-att (/   0 127)
         :amp-dec (/  27 127)
         :amp-sus (/  95 127)
         :amp-rel (/  26 127)
         :q       (/  11 127)
         :detune  (/  59 127)
         :mix     (/  67 127)}))

(comment
  (def bass-midi-player
    (setup-inst-midi passive-bass-patch bass-mapping bass-state))
  (midi-player-stop bass-midi-player)
  (println bass-state)
  (stop)
  (comment))

(def bass-lein-player
  (setup-inst-lein passive-bass-patch bass-mapping bass-state))

(defmethod live/play-note :bass [{midi :pitch dur :duration vel :velocity :as msg}]
  (bass-lein-player msg))

(def passive-bass-melody
  (->>
   (phrase [7, 1/2, 9/2, 4]
           [0,  -1,   1, 2])
   (all :velocity 127)
   (all :part :bass)
   (where :pitch (comp (scale/from -36) scale/B scale/blues))))

(def passive-drum-bass
  (->>
    (with clap-melody kick-melody)
    (with passive-bass-melody)
    (tempo (bpm 120))))

(comment
  (live/jam (var passive-drum-bass))
  (start-recording)
  (live/stop)
  (stop-recording))
