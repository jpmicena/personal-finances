(ns personal-finances.logic.coercion
  (:require [personal-finances.models.account :as m-acc]))

(defn int-like
  "Tries to parse a String to integer, returns nil if it's not possible"
  [s]
  (try (Integer/parseInt s)
       (catch Exception _ nil)))

(defn double-like
  "Tries to parse a String to a double, returns nil if it's not possible"
  [s]
  (try (Double/parseDouble s)
       (catch Exception _ nil)))

(defn date-like
  "Tries to parse a String to a date, returns nil if it's not possible"
  [s]
  (try (-> s java.time.LocalDate/parse .toString)
       (catch Exception _ nil)))

(defn account-like
  "Tries to parse a String of the format category:account-name
  (returning a map of :category :name), verifies if category is valid
  returns nil if it's not possible"
  [s]
  (let [m (->> s
               (re-matches #"(\w*):(\w*)")
               rest
               (zipmap [:category :name]))]
    (if (some #{(:category m)} m-acc/categories) m nil)))



