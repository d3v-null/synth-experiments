(ns synth-experiments.bass
    (:require [overtone.live :refer :all]
              [synth-experiments.midi :refer :all]
              [overtone.studio.scope :refer :all]
              [overtone.inst.synth :refer :all]))

;; two detuned saws passed thru filters
;; http://music.tutsplus.com/tutorials/programming-essential-subtractive-synth-patches--audio-8962
(definst bass-patch  [note {:default 60 :min 0 :max 127 :step 1}
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
                      gate 1]
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

; (def bass-knobs
;   {
;     :detune 10
;     ; :dephase 11
;     :q 11
;     :lpf-att 91
;     :lpf-rel 93
;     :amp-att 73
;     :amp-rel 72
;     :cutoff 74
;     :mix 71})

(def bass-knobs
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

(def bass-atom (atom nil))
(def bass-midi-atom (atom nil))

(comment
  (scope :audio-bus 1)
  (setup-inst bass-patch bass-knobs bass-atom bass-midi-atom)
  (midi-player-stop @bass-midi-atom)
  (comment))

(comment
  (overtone.inst.synth/bass))
