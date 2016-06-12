(ns synth-experiments.piano
  (:require [overtone.live :refer :all]
            [synth-experiments.midi :refer :all]
            [overtone.inst.piano :refer :all]))

(def piano-knobs
  { :pan 10
    :exp 11
    :rev 91
    :cho 93
    :decay 73
    :release 72
    :tune 74
    :sustain 71})

(comment
  (def piano-fn (atom nil))
  (def piano-midi-fn (atom nil))
  (setup-inst piano piano-knobs piano-fn piano-midi-fn)
  (midi-player-stop @piano-midi-fn)
  (comment))
