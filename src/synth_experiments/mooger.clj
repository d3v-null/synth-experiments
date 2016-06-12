(ns synth-experiments.mooger
  (:require [overtone.live :refer :all
             :rename {midi-inst-controller bad-midi-inst-controller}]
            [synth-experiments.midi :refer :all]))

(definst mooger
  "Choose 0, 1, or 2 for saw, sin, or pulse"
  [note {:default 60 :min 0 :max 127 :step 1}
   amp  {:default 0.3 :min 0 :max 1 :step 0.01}
   osc1 {:default 0 :min 0 :max 2 :step 1}
   osc2 {:default 1 :min 0 :max 2 :step 1}
   osc1-level {:default 0.5 :min 0 :max 1 :step 0.01}
   osc2-level {:default 0 :min 0 :max 1 :step 0.01}
   cutoff {:default 1/40 :min 0 :max 1}
   attack {:default 0.0001 :min 0.0001 :max 5 :step 0.001}
   decay {:default 0.3 :min 0.0001 :max 5 :step 0.001}
   sustain {:default 0.99 :min 0.0001 :max 1 :step 0.001}
   release {:default 0.0001 :min 0.0001 :max 6 :step 0.001}
   fattack {:default 0.0001 :min 0.0001 :max 6 :step 0.001}
   fdecay {:default 0.3 :min 0.0001 :max 6 :step 0.001}
   fsustain {:default 0.999 :min 0.0001 :max 1 :step 0.001}
   frelease {:default 0.0001 :min 0.0001 :max 6 :step 0.001}
   gate 1]
  (let [freq       (midicps note)
        osc-bank-1 [(saw freq) (sin-osc freq) (pulse freq)]
        osc-bank-2 [(saw freq) (sin-osc freq) (pulse freq)]
        amp-env    (env-gen (adsr attack decay sustain release) gate :action FREE)
        f-env      (env-gen (adsr fattack fdecay fsustain frelease) gate)
        s1         (* osc1-level (select osc1 osc-bank-1))
        s2         (* osc2-level (select osc2 osc-bank-2))
        filt       (moog-ff (+ s1 s2) (* 20000 cutoff f-env) 3)]
    (* amp filt)))

(def mooger-keys
  { :osc1-level 10
    :osc2-level 11
    :fattack 91
    :frelease 93
    :attack 73
    :release 72
    :cutoff 74})
    ; :res 71})

(comment
  (def mooger-player-atom (atom nil))
  (def mooger-midi-fn (atom nil))
  (setup-inst mooger mooger-keys mooger-player-atom mooger-midi-fn)
  (midi-player-stop @mooger-midi-fn)
  (comment))
