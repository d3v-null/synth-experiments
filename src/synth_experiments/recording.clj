(ns synth-experiments.recording
  (:require [overtone.live :refer [recording-start recording-stop]]
            [clj-time.format :as f]
            [clj-time.core :as t]
            [clj-time.local :as l]))

(defn get-time-str []
  (f/unparse (f/formatter "yyyyMMddhhmmss") (l/local-now)))

(def recording-dir "~/Music/overtone/")

(defn start-recording []
  (recording-start
   (clojure.string/join [recording-dir "test" (get-time-str) ".wav"])))

(defn stop-recording []
  (recording-stop))
