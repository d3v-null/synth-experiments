(ns synth-experiments.strings
    (:require [overtone.live :refer :all
               :rename {midi-inst-controller bad-midi-inst-controller}]
              [synth-experiments.midi :refer :all]))

(definst violin-patch [note 60 amp 0.5 exp 0.4 gate 1
                       lfo-rate 4/10 lfo-att 5/10
                       amp-att 0.2 amp-dec 0.5 amp-sus 0.8 amp-rel 0.2
                       ctl-lfo 4/10 hpff 5/10]
  (let [freq (midicps note)
        lfo-env (env-gen (asr lfo-att :release (/ amp-rel 2)) :gate gate)
        lfo-sig (sin-osc (* lfo-rate 40))
        amp-env (env-gen (adsr amp-att amp-dec amp-sus amp-rel)
                         :gate gate :action FREE)
        ctl-env (+ (* ctl-lfo lfo-sig) (* (- 1 ctl-lfo) amp-env))]
    (-> (saw freq)
        (* ctl-env)
        (hpf (* hpff 1000)))))

(def violin-knobs
  {
    ; :pan 10
    :exp 11
    :lfo-rate 91
    :lfo-att 93
    :amp-att 73
    :amp-rel 72
    :hpff 74
    :ctl-lfo 71})


(comment
  (def violin-atom (atom nil))
  (def violin-midi-atom (atom nil))
  (setup-inst violin-patch violin-knobs violin-atom violin-midi-atom)
  (midi-player-stop @violin-midi-atom)
  (comment))


(definst bass-patch [note 60 amp 0.5 exp 0.4 gate 1
                       lfo-rate 4/10 lfo-att 5/10
                       amp-att 0.2 amp-dec 0.5 amp-sus 0.8 amp-rel 0.2
                       ctl-lfo 4/10 hpff 5/10]
  (let [freq (midicps note)
        lfo-env (env-gen (asr lfo-att :release (/ amp-rel 2)) :gate gate)
        lfo-sig (sin-osc (* lfo-rate 40))
        amp-env (env-gen (adsr amp-att amp-dec amp-sus amp-rel)
                         :gate gate :action FREE)
        ctl-env (+ (* ctl-lfo lfo-sig) (* (- 1 ctl-lfo) amp-env))]
    (-> (saw freq)
        (* ctl-env)
        (hpf (* hpff 1000)))))

(def bass-knobs
  {
    ; :pan 10
    :exp 11
    :lfo-rate 91
    :lfo-att 93
    :amp-att 73
    :amp-rel 72
    :hpff 74
    :ctl-lfo 71})


(comment
  (def bass-atom (atom nil))
  (def bass-midi-atom (atom nil))
  (setup-inst bass-patch bass-knobs bass-atom bass-midi-atom)
  (midi-player-stop @bass-midi-atom)
  (comment))
