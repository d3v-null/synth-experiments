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
  (def piano-state (atom {:sustain 0.0}))
  (def piano-midi-player
    (setup-inst-midi piano piano-mapping piano-state))
  (swap! piano-state assoc :sustain 1.0)
  (midi-player-stop piano-midi-player)
  (comment))
