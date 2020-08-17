(ns personal-finances.logic.utils)

(defn success?
  [m]
  (contains? m :success))

(defn success
  [m]
  (:success m))

(defn print-result
  "Prints the result of a controller into the console
  Applies success-fn to the success value.
  If there's no success value, then prints the failure"
  [success-fn result]
  (if-let [sr (success result)]
      (println (success-fn sr))
      (println (:failure result))))

(defn update-if-exists
  [m k f]
  (if (contains? m k)
    (update m k f)
    m))
