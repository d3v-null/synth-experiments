(ns synth-experiments.bass
    (:require [overtone.live :refer :all
               :rename {midi-inst-controller bad-midi-inst-controller}]
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

(comment
  (scope :audio-bus 1)
  (def bass-state (atom {}))
  (def bass-midi-player
    (setup-inst-midi bass-patch bass-mapping bass-state))
  (midi-player-stop bass-player)
  (comment))
