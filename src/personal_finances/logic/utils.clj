(ns personal-finances.logic.utils)

(defn success?
  [m]
  (contains? m :success))

(defn success
  [m]
  (:success m))

