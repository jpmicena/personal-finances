(ns personal-finances.adapters.coercion)

(defn int-like
  "Tries to parse a String to integer, returns nil if it's not possible"
  [i]
  (try (Integer/parseInt i)
       (catch Exception _ nil)))
