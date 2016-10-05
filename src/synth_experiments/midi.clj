(ns synth-experiments.midi
  (:require [overtone.live :refer :all
             :rename {midi-inst-controller bad-midi-inst-controller}]
            [overtone.at-at :as at-at]
            [overtone.midi :as midi]
            [overtone.studio.scope :refer :all]))

(defn- midi-control-handler
  [state-atom handler mapping msg]
  (let [note (:note msg)]
    (when (contains? mapping note)
      (let [[ctl-name scale-fn] (get mapping note)
            ctl-val (scale-fn (:velocity msg))]
        (swap! state-atom assoc ctl-name ctl-val)
        (handler ctl-name ctl-val)))))

(defn midi-inst-controller-good
  "overrides broken midi-inst-controller from Overtone"
  [state-atom handler mapping]
  (let [ctl-key (keyword (gensym 'control-change))]
    (on-event [:midi :control-change]
              #(midi-control-handler state-atom handler mapping %)
              ctl-key)))

(defn get-midi-player
  [ctl-inst mapping state-atom]
  (fn [& {note :note amp :amp vel :velocity}]
      (println note amp)
      (let [ play-params [note :amp amp :velocity vel]
             play-params
              (reduce-kv
                (fn [params param value]
                  (into params [param value]))
                play-params
                @state-atom)]
          (apply ctl-inst play-params))))

(defn setup-inst-midi
  [ctl-inst mapping state-atom]
  (midi-inst-controller-good state-atom (partial ctl ctl-inst) mapping)
  (midi-poly-player (get-midi-player ctl-inst mapping state-atom)))

(def midi-player-pool-good (at-at/mk-pool))

(defn setup-inst-lein
  [ctl-inst mapping state-atom]
  (fn [{note :pitch vel :velocity seconds :duration :as msg}]
    (println "lein playing message" msg)
    (let
      [amp (/ vel 127)
       midi-player (get-midi-player ctl-inst mapping state-atom)
       node (midi-player :note note :amp amp :velocity vel)
       msecs (* seconds 1000)]
      (at-at/after msecs #(node-control node [:gate 0]) midi-player-pool-good))))

(def divide127 #(/ % 127.0))
(def inv-divide127 #(- 1 (/ % 127.0)))

(definst sinder [note 60 amp 0.5 exp 0.5 gate 1]
  (let [freq (midicps note)
        lvl (* amp exp)
        env-amp (env-gen (adsr 0.1 :level lvl) gate :action FREE)]
    (-> (sin-osc freq)
        (* env-amp))))

(comment
  ;; how to use midi
  (scope :audio-bus 1)
  (def sinder-state (atom {:exp 0.9}))
  (def sinder-mapping
    {10 [:exp divide127]})
  (def sinder-midi-player
    (setup-inst-midi sinder sinder-mapping sinder-state))
  (swap! sinder-state assoc :exp 0.1)
  (midi-player-stop sinder-midi-player)
  (def sinder-lein-player
    (setup-inst-lein sinder sinder-mapping sinder-state))
  (sinder-lein-player {:pitch 70 :velocity 100 :duration 100})
  (stop)
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
    12 [:f1 inv-divide127]
    13 [:f2 inv-divide127]
    14 [:f3 inv-divide127]
    15 [:f4 inv-divide127]
    16 [:f5 inv-divide127]
    17 [:f6 inv-divide127]
    18 [:f7 inv-divide127]
    19 [:f8 inv-divide127]
    20 [:f9 inv-divide127]})
