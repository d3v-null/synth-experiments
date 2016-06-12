(ns synth-experiments.core
  (:require [overtone.live :refer :all
             :rename {midi-inst-controller bad-midi-inst-controller}]
            [synth-experiments.midi :refer :all]))
