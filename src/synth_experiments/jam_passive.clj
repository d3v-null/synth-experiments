(ns synth-experiments.jam-passive
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
   [synth-experiments.midi :refer :all]))

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

;; BASS

(import-recent-values
  {70 89, 74 116, 20 120, 72 68, 15 35, 75 4, 13 64, 17 28, 12 5, 19 26,
   11 127, 14 30, 16 0, 73 59, 18 95, 71 67})

(definst passive-bass-patch  [note {:default 60 :min 0 :max 127 :step 1}]
                      amp {:default 0.5 :min 0.0 :max 1.0}
                      exp {:default 0.8 :min 0.0 :max 1.0}
                      ;; 0 -> detune down by 1/2 semi, 1 -> detune up...
                      detune {:default 0.6 :min 0.0 :max 1.0}
                      ; dephase {:default 0.5 :min 0.0 :max 1.0}
                      amp-att 0.2 amp-dec 0.2 amp-sus 0.6 amp-rel 0.2
                      lpf-att 0.5 lpf-dec 0.2 lpf-sus 0.4 lpf-rel 0.2
                      cutoff 0.4
                      mix 0.5
                      q 0.5
                      gate 1
  (let [freq1 (midicps note)
        freq2 (midicps (+ note detune -0.5))
        amp-env (env-gen (adsr amp-att :release amp-rel :level amp)
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

(def passive-bass-knobs
  {
    :detune 73
    ; :dephase 11
    :q 11
    :cutoff 74
    :mix 71
    :lpf-att 12
    :lpf-dec 13
    :lpf-sus 14
    :lpf-rel 15
    :amp-att 16
    :amp-dec 17
    :amp-sus 18
    :amp-rel 19
    :exp 20})

(def passive-bass-atom (atom nil))
(def passive-bass-midi-atom (atom nil))

(@passive-bass-atom :note 60 :amp 0.5 :vel 10)

(comment
  (scope :audio-bus 1)
  (setup-inst passive-bass-patch passive-bass-knobs
              passive-bass-atom passive-bass-midi-atom)
  (midi-player-stop @passive-bass-midi-atom)
  (comment))
