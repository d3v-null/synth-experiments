(ns synth-experiments.filters
    (:require [overtone.live :refer :all
               :rename {midi-inst-controller bad-midi-inst-controller}]
              [synth-experiments.midi :refer :all]
              [overtone.studio.scope :refer :all]
              [overtone.inst.synth :refer :all]))

;; two detuned saws passed thru filters
;; http://music.tutsplus.com/tutorials/programming-essential-subtractive-synth-patches--audio-8962
(definst filter-patch  [note {:default 60 :min 0 :max 127 :step 1}
                        amp {:default 0.5 :min 0.0 :max 1.0}
                        amp-att 0.2 amp-dec 0.2 amp-sus 0.6 amp-rel 0.2
                        q 0.5
                        gate 1]
  (let [freq (midicps note)
        amp-env (env-gen (adsr amp-att amp-dec amp-sus amp-rel amp)
                         :gate gate :action FREE)
        noise (white-noise)]
    (-> noise
        (resonz freq q)
        (* amp-env))))

(def filter-mapping
  {11 [:q divide127]
   73 [:amp-att divide127]
   72 [:amp-rel divide127]})

(comment
  (def filter-state (atom {}))
  (def filter-midi-player
    (setup-inst-midi filter-patch filter-mapping filter-state))
  (midi-player-stop filter-player)
  (comment))
