(ns synth-experiments.midi
  (:require [overtone.live :refer :all
             :rename {midi-inst-controller bad-midi-inst-controller}]))

(defn midi-control-handler
  [state-atom handler mapping msg]
  (let [note (:note msg)]
    (when (contains? mapping note)
      (let [[ctl-name scale-fn] (get mapping note)
            ctl-val (scale-fn (:velocity msg))]
        (swap! state-atom assoc ctl-name ctl-val)
        (handler ctl-name ctl-val)))))

(comment
  (def sinder-state (atom {}))
  (def sinder-mapping
    {10 [:exp #(/ % 127.0)]})
  (println @sinder-state)
  (midi-control-handler
   sinder-state
   (partial println)
   sinder-mapping
   {:note 10 :velocity 75}))

(defn midi-inst-controller
  "overrides broken midi-inst-controller from Overtone"
  [state-atom handler mapping]
  (let [ctl-key (keyword (gensym 'control-change))]
    (on-event [:midi :control-change]
              #(midi-control-handler state-atom handler mapping %)
              ctl-key)))

(defn setup-inst
  [ctl-inst mapping state-atom]
  (midi-inst-controller state-atom (partial ctl ctl-inst) mapping)
  (let [inst-player
        (fn [& {note :note amp :amp vel :velocity}]
            (let [play-params
                  (reduce-kv
                    (fn [params param value]
                      (into params [param value]))
                    [note :amp amp :velocity vel]
                    @state-atom)]
              (apply ctl-inst play-params)))]
    (midi-poly-player inst-player)))

(def divide127 #(/ % 127.0))
(def inv-divide127 #(- 1 (/ % 127.0)))

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


(def mk-255c-mapping
  {10 [:pan divide127]
   11 [:exp divide127]
   91 [:rev divide127]
   93 [:cho divide127]
   73 [:att divide127]
   72 [:rel divide127]
   74 [:cut divide127]
   71 [:res divide127]})

(def uc-33-mapping
  { 73 [:c10 divide127]
    74 [:c11 divide127]
    11 [:c12 divide127]
    8  [:c14 divide127]
    10 [:c15 divide127]
    9  [:c16 divide127]
    3  [:c17 divide127]
    72 [:c18 divide127]
    75 [:c19 divide127]
    78 [:c20 divide127]
    79 [:c21 divide127]
    91 [:c22 divide127]
    92 [:c23 divide127]
    93 [:c24 divide127]
    90 [:c25 divide127]
    70 [:c26 divide127]
    71 [:c27 divide127]
    76 [:c28 divide127]
    7  [:c29 divide127]
    81 [:c30 divide127]
    82 [:c31 divide127]
    83 [:c32 divide127]
    80 [:c33 divide127]
    12 [:f1 divide127]
    13 [:f2 divide127]
    14 [:f3 divide127]
    15 [:f4 divide127]
    16 [:f5 divide127]
    17 [:f6 divide127]
    18 [:f7 divide127]
    19 [:f8 divide127]
    20 [:f9 divide127]})
