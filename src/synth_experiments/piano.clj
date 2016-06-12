(ns synth-experiments.piano
  (:require [overtone.live :refer :all
             :rename {midi-inst-controller bad-midi-inst-controller}]
            [synth-experiments.midi :refer :all]
            [overtone.inst.piano :refer :all]))

(def piano-mapping
  { 10 [:pan divide127]
    11 [:exp divide127]
    91 [:rev divide127]
    93 [:cho divide127]
    73 [:decay divide127]
    72 [:release divide127]
    74 [:tune divide127]
    71 [:sustain divide127]})

(comment
  (def piano-state (atom {}))
  (def piano-player
    (setup-inst piano piano-mapping piano-state))
  (midi-player-stop piano-player)
  (comment))

(comment
  ;; how to use midi
  (definst sinder [note 60 amp 0.5 exp 0.5 gate 1]
    (let [freq (midicps note)]
      (-> (sin-osc freq)
          (* (env-gen (adsr 0.1) gate :action FREE))
          (* amp exp))))
  (def sinder-state (atom {}))
  (def sinder-mapping
    {10 [:exp divide127]})
  (def sinder-player
    (setup-inst sinder sinder-mapping sinder-state))
  (midi-player-stop sinder-player)
  (comment))
