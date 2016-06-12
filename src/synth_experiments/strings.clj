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

(def violin-mapping
  {
    11 [:exp divide127]
    91 [:lfo-rate divide127]
    93 [:lfo-att divide127]
    73 [:amp-att divide127]
    72 [:amp-rel divide127]
    74 [:hpff divide127]
    71 [:ctl-lfo divide127]})

(comment
  (def violin-state (atom {}))
  (def violin-player
    (setup-inst violin-patch violin-mapping violin-state))
  (midi-player-stop violin-player)
  (comment))
