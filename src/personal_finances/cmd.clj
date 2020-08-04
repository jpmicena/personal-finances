(ns personal-finances.cmd
  (:require [clojure.string     :as string]
            [clojure.spec.alpha :as s]
            [personal-finances.controllers.account :as ctrl-acc]))

(s/def ::name (s/coll-of string?))
(s/def ::fn   fn?)
(s/def ::args-spec s/regex?)
(s/def ::handler (s/keys :req [::name ::fn ::args-spec]))
(s/def ::handler-list (s/coll-of ::handler))

(def handlers [ctrl-acc/account-add-handler
               ctrl-acc/account-list-handler
               ctrl-acc/account-remove-handler])

(defn- get-cmd-map
  "Mapping of existing commands"
  [handlers]
  {:pre [(s/valid? ::handler-list handlers)]}
  (reduce (fn [m handler] (assoc-in m (::name handler) handler)) {} handlers))

(def ^:private cmd-map (get-cmd-map handlers))

(defn- parse-cmd!
  "Parse text in command line, returns a vector with [handler [args]]"
  [line]
  (let [tokens (string/split line #" ")]
    (loop [parsed []
           to-parse tokens]
      (let [cur-token (first to-parse)
            parsing   (conj parsed cur-token)
            cmd       (get-in cmd-map parsing)]
        (cond (s/valid? ::handler cmd)  [cmd (rest to-parse)]
              (nil? cmd)                (throw (Exception. "Command not found")) ;; TODO: Replace with helper for not found commands
              :else                     (recur parsing (rest tokens)))))))

(defn- apply-cmd
  "Tries to apply the parsed-cmd, conforms args to args-spec"
  [[{:keys [::fn ::args-spec]} args] system]
  (let [conformed-args (s/conform args-spec (conj (vec args) system))]
    (if (s/invalid? conformed-args)
      (s/explain args-spec args) ;; Strangely I need to do this so the print is not lagging
      (fn conformed-args))))

(defn execute-cmd
  [line system]
  (try
    (-> line
        parse-cmd!
        (apply-cmd system))
    (catch Exception e
      (println (ex-message e)))))

(comment
(parse-cmd! "account list"))
