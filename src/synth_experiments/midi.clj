(ns synth-experiments.midi
  (:require [overtone.live :refer :all
             :rename {midi-inst-controller bad-midi-inst-controller}]))

; (def knob-default-value 63)
; (def knob-max-value 127)
; (def recent-values (atom {}))
; (defn update-recent-value
;   [chan value]
;   (swap! recent-values #(assoc % chan value)))
; (defn get-recent-chan-coef
;   [chan]
;   (/ (get @recent-values chan knob-default-value) knob-max-value))
; (defn get-knob [chan knob-mapping]
;   (first (keep #(when (= (val %) chan) (key %)) knob-mapping)))
; (defn get-param [chan mapping]
;   (first (get mapping chan)))
; (defn get-chan [param mapping]
;   (first (keep #(when (= (first (val %)) chan) (key %)) mapping)))
; (defn dump-recent-values []
;   (println @recent-values))
; (defn import-recent-values [new-recent-values]
;   (reset! recent-values new-recent-values))

; (def test-mapping {:a 1 :b 2})
; (contains? test-mapping :a)


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
  "Create a midi instrument controller for manipulating the parameters of an instrument
  using an external device.  Requires an atom to store the state of the parameters, a
  handler that will be called each time a parameter is modified, and a mapping table to
  specify how midi control messages should manipulate the parameters.
  (def ding-mapping
    {22 [:attack     #(* 0.3 (/ % 127.0))]
     23 [:decay      #(* 0.6 (/ % 127.0))]
     24 [:sustain    #(/ % 127.0)]
     25 [:release    #(/ % 127.0)]})
  (def ding-state (atom {}))
  (midi-inst-controller ding-state (partial ctl ding) ding-mapping)
  "
  [state-atom handler mapping]
  (let [ctl-key (keyword (gensym 'control-change))]
    (on-event [:midi :control-change]
              #(midi-control-handler state-atom handler mapping %)
              ctl-key)))

(def mk-255c-mapping
  {10 [:exp #(/ % 127.0)]})

; (defn add-recent-value-updater []
;   (on-event [:midi :control-change]
;             (fn [{chan :note value :velocity}]
;               (update-recent-value chan value))
;             ::handle-midi-cc))

(defn setup-inst
  [ctl-inst mapping state-atom]
  (midi-inst-controller state-atom (partial ctl ctl-inst) mapping)
  (let [inst-player
        (fn [& {note :note amp :amp vel :velocity}]
            (let [play-params
                  (reduce-kv
                    (fn [params param value]
                      (into params param value))
                    [note :amp amp :velocity vel]
                    @state-atom)]
              (apply ctl-inst play-params)))]
    (midi-poly-player inst-player)))

(definst sinder [note 60 amp 0.5 exp 0.5 gate 1]
  (let [freq (midicps note)]
    (-> (sin-osc freq)
        (* (env-gen (adsr 0.1) gate :action FREE))
        (* amp exp))))


(comment
  (def sinder-state (atom {}))
  (def sinder-mapping
    {10 [:exp #(/ % 127.0)]})
  (println @sinder-state)
  (def sinder-player
    (setup-inst sinder sinder-mapping sinder-state))
  (midi-player-stop sinder-player)
  (comment))


;; delete this

; (comment
;   (definst sinder [note 60 amp 0.5 exp 0.5 gate 1]
;     (let [freq (midicps note)]
;       (-> (sin-osc freq)
;           (* (env-gen (adsr 0.1 :level exp) gate :action FREE))
;           (* amp))))
;   (def sinder-state (atom {}))
;   (def sinder-mapping
;     {10 [:exp #(/ % 127.0)]})
;   (midi-inst-controller sinder-state (partial ctl sinder) sinder-mapping)
;   (println @sinder-state))



;; controller numbers


(def mk-255c-knobs
  {
    :pan 10 ;dup
    :exp 11 ;dup
    :rev 91 ;dup
    :cho 93 ;dup
    :att 73 ;dup
    :rel 72 ;dup
    :cut 74 ;dup
    :res 71}) ;dup

(def uc-33-knobs
  {
   :c10 73 ;dup
   :c11 74 ;dup
   :c12 11 ;dup
  ;  :c13
   :c14 8
   :c15 10 ;dup
   :c16 9
   :c17 3
   :c18 72 ;dup
   :c19 75
   :c20 78
   :c21 79
   :c22 91 ;dup
   :c23 92 ;dup
   :c24 93
   :c25 90
   :c26 70
   :c27 71 ;dup
   :c28 76
   :c29 7
   :c30 81
   :c31 82
   :c32 83
   :c33 80
   :f1 12
   :f2 13
   :f3 14
   :f4 15
   :f5 16
   :f6 17
   :f7 18
   :f8 19
   :f9 20})


; (defn update-inst-param
;   [inst knob-mapping chan]
;   (let [knob (get-knob chan knob-mapping)]
;     (if knob
;       (do (ctl inst knob (get-recent-chan-coef chan))))))
;
; (defn knob-presets [knob-mapping]
;  (reduce-kv
;   (fn [presets knob chan]
;     (concat presets [knob (get-recent-chan-coef chan)]))
;   ()
;   knob-mapping))

; (definst sinder [note 60 amp 0.5 exp 0.5 gate 1]
;   (let [freq (midicps note)]
;     (-> (sin-osc freq)
;         (* (env-gen (adsr 0.1) gate :action FREE))
;         (* amp exp))))

; (defn setup-inst
;   [ctl-inst knob-mapping inst-fn-ref midi-fn-ref]
;   (on-event [:midi :control-change]
;             (fn [{chan :note value :velocity}]
;               (let [knob (get-knob chan knob-mapping)]
;                 (if knob
;                   (println "updating" knob value)))
;               (update-recent-value chan value)
;               (update-inst-param ctl-inst knob-mapping chan))
;             ::handle-midi-cc)
;   (let [inst-fn (fn [& {note :note amp :amp vel :velocity}]
;                   (println "playing" note amp)
;                   (let [note-params [note]])
;                   (apply ctl-inst
;                          (concat [note :amp amp :vel vel]
;                                  (knob-presets knob-mapping))))]
;       (reset! inst-fn-ref inst-fn)
;       (reset! midi-fn-ref (midi-poly-player @inst-fn-ref))))

; (defn setup-inst
;   [ctl-inst mapping])


;;listen for control changes

; (defn listen-cc []
;   (on-event [:midi :control-change]
;             (fn [{chan :note value :velocity}]
;               (println "updating" chan value))
;             ::handle-midi-cc))

; (comment
;   (def sinder-fn (atom nil))
;   (def midi-fn (atom nil))
;   (setup-inst sinder mk-255c-knobs sinder-fn midi-fn)
;   (ctl sinder :exp 0)
;   (midi-player-stop @midi-fn)
;   (comment))
