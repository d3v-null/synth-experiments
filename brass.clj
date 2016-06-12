(ns synth-experiments.brass
    (:require [/overtone.live :refer :all]
              [synth-experiments.midi :refer :all]))

(definst brass-patch [note 60 amp 0.5 exp 0.4 gate 1
                      amp-att 0.2 amp-dec 0.2 amp-sus 0.6 amp-rel 0.2
                      lpf-att 0.5 lpf-dec 0.2 lpf-sus 0.6 lpf-rel 0.2
                      cutoff 0.4]
  (let [freq (midicps note)
        amp-env (env-gen (adsr amp-att amp-dec amp-sus amp-rel)
                         :gate gate :action FREE)
        lpf-env (env-gen (adsr lpf-att lpf-dec lpf-sus lpf-rel)
                         :gate gate)
        lpf-sig (* cutoff 1000 lpf-env)]
    (-> (saw freq)
        (* amp-env)
        (lpf lpf-sig))))

(def brass-knobs
  {
    ; :pan 10
    :exp 11
    :lpf-att 91
    :lpf-rel 93
    :amp-att 73
    :amp-rel 72
    :cutoff 74
    :lpf-sus 71})


(comment
  (def brass-atom (atom nil))
  (def brass-midi-atom (atom nil))
  (setup-inst brass-patch brass-knobs brass-atom brass-midi-atom)
  (midi-player-stop @brass-midi-atom)
  (comment))
