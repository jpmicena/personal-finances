(ns personal-finances.cmd
  (:require [clojure.string     :as string]
            [clojure.spec.alpha :as s]))

(s/def ::name (s/coll-of string?))
(s/def ::fn   fn?)
(s/def ::args-spec s/get-spec)
(s/def ::handler (s/keys :req [::name ::fn ::args-spec]))
(s/def ::handler-list (s/coll-of ::handler))

(def handlers [])

(defn- get-cmd-map
  "Mapping of existing commands"
  [handlers]
  {:pre [(s/valid? ::handler-list handlers)]}
  (reduce (fn [m handler] (assoc-in m (::name handler) handler)) {} handlers))

(def ^:private cmd-map (get-cmd-map handlers))

(defn- parse-cmd!
  "Parse text in command line, returns a vector with [handler [args]]"
  [line cmd-map]
  (let [tokens (string/split line #" ")]
    (loop [parsed []
           to-parse tokens]
      (let [cur-token (first to-parse)
            parsing   (conj parsed cur-token)
            cmd       (get-in cmd-map parsing)]
        (cond (s/valid? ::handler cmd)  [cmd (rest to-parse)]
              (nil? cmd)                (throw (Exception. "Command not found")) ;; TODO: Replace with helper for not found commands
              :else                     (recur parsing (rest tokens)))))))

(defn- apply-cmd!
  "Tries to apply the parsed-cmd, conforms args to args-spec"
  [[{:keys [::fn ::args-spec]} args]]
  (let [conformed-args (s/conform args-spec args)]
    (if (s/invalid? conformed-args)
      (s/explain args-spec args)
      (fn conformed-args))))

(defn execute-cmd!
  [line cmd-map]
  (try
    (-> line (parse-cmd! cmd-map) apply-cmd!)
    (catch Exception e
      (println (ex-message e)))))

(comment
(execute-cmd! "account" cmd-map)
)
